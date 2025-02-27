import React, { useEffect, useRef, useState } from "react";
import InfoSection from "../../components/parent/common/InfoSection";
import daramgi from "../../assets/parent/bus-daramgi.png";
import busIcon from "../../assets/parent/bus-driving.gif";
import { receiveBusLocation } from "../../api/webSocket";
import {
  postKidBoardingStatus,
  getKidBoardingStatus,
  getAllBusStops,
} from "../../api/bus";
import { getParentInfo } from "../../api/info";
import { Toggle } from "../../components/parent/bus/Toggle";
import { FaBus } from "react-icons/fa";
import { MdGpsFixed } from "react-icons/md";
import { useParentInfoStore } from "../../stores/useParentInfoStore";
import LoadingSpinner from "../../components/common/LoadingSpinner";

declare global {
  interface Window {
    kakao: any;
  }
}

export default function ParentBus() {
  const mapContainer = useRef<HTMLDivElement>(null);
  const wsRef = useRef<WebSocket | null>(null);
  const [location, setLocation] = useState<{ lat?: number; lng?: number }>({
    lat: 37.5665,
    lng: 126.978,
  });
  const [isBoarding, setIsBoarding] = useState(false);
  const [loading, setLoading] = useState(false);
  const [isMoving, setIsMoving] = useState(false);
  const [busOption, setBusOption] = useState("등원"); // 새로운 상태 추가
  const [childId, setChildId] = useState<number | null>(null);
  const [kindergartenId, setKindergartenId] = useState(null);
  const [busStops, setBusStops] = useState([]);
  const mapRef = useRef<any>(null);
  const busMarkerRef = useRef<any>(null);
  const parentMarkerRef = useRef<any>(null);
  const [parentLocation, setParentLocation] = useState<{
    latitude?: number;
    longitude?: number;
  }>({});
  const { parentInfo, setParentInfo } = useParentInfoStore();
  const intervalIdRef = useRef<NodeJS.Timeout | null>(null);
  const isWebSocketInitialized = useRef<boolean>(false);
  let centerFlag = false;
  const busCenterFlag = useRef<boolean>(false);

  useEffect(() => {
    async function fetchParentInfo() {
      try {
        let currentKindergartenId = kindergartenId;
        if (!currentKindergartenId) {
          const fetchedParentInfo = await getParentInfo();
          setParentInfo(fetchedParentInfo);
          currentKindergartenId =
            fetchedParentInfo.child.kindergartenClass.kindergartenClassId;
          setKindergartenId(currentKindergartenId);
          const fetchedAllBusStopsInfo = await getAllBusStops(
            currentKindergartenId
          );
          setBusStops(fetchedAllBusStopsInfo);
        } else {
          setKindergartenId(currentKindergartenId);
          const fetchedAllBusStopsInfo = await getAllBusStops(kindergartenId);
          setBusStops(fetchedAllBusStopsInfo);
        }
      } catch (error) {
        console.log("유치원 ID 조회 실패", error);
      }
    }
    fetchParentInfo();
  }, [kindergartenId, setParentInfo]);

  useEffect(() => {
    if (mapRef.current && busStops.length > 0) {
      const infowindow = new window.kakao.maps.InfoWindow({
        content: "",
      });

      busStops.forEach((busStop) => {
        const busStopPosition = new window.kakao.maps.LatLng(
          busStop.latitude,
          busStop.longitude
        );

        const busStopMarker = new window.kakao.maps.Marker({
          map: mapRef.current,
          position: busStopPosition,
          title: busStop.busStopName,
        });

        window.kakao.maps.event.addListener(
          busStopMarker,
          "click",
          function () {
            infowindow.setContent(`<div style="padding:5px;">${busStop.busStopName}</div>
            `);
            infowindow.open(mapRef.current, busStopMarker);
          }
        );
      });

      window.kakao.maps.event.addListener(mapRef.current, "click", function () {
        infowindow.close();
      });
    }
  }, [mapRef.current, busStops]);

  const initializeMap = () => {
    if (mapRef.current || !mapContainer.current) {
      return;
    }

    const container = mapContainer.current;
    const options = {
      center: new window.kakao.maps.LatLng(location.lat, location.lng),
      level: 3,
    };
    const newMap = new window.kakao.maps.Map(container, options);
    mapRef.current = newMap;

    const initialPosition = new window.kakao.maps.LatLng(
      location.lat,
      location.lng
    );

    const busMarkerInstance = new window.kakao.maps.CustomOverlay({
      position: initialPosition,
      content: `
        <div style="position: relative; width: 64px; height: 64px;">
          <img src="${busIcon}" width="64" height="64" />
        </div>
      `,
      yAnchor: 0.5,
      xAnchor: 0.5,
      zIndex: 1,
    });

    busMarkerInstance.setMap(newMap);
    busMarkerRef.current = busMarkerInstance;

    const parentInitialPosition = new window.kakao.maps.LatLng(
      location.lat,
      location.lng
    );

    const overlayContent = document.createElement("div");
    overlayContent.style.position = "relative";
    overlayContent.style.width = "50px";
    overlayContent.style.height = "50px";
    const pulseRing = document.createElement("div");
    pulseRing.className = "pulse-ring";
    overlayContent.appendChild(pulseRing);
    const markerIcon = document.createElement("img");
    markerIcon.src = daramgi;
    markerIcon.style.position = "absolute";
    markerIcon.style.top = "50%";
    markerIcon.style.left = "50%";
    markerIcon.style.width = "40px";
    markerIcon.style.height = "60px";
    markerIcon.style.transform = "translate(-50%, -50%)";
    overlayContent.appendChild(markerIcon);
    const parentMarkerInstance = new window.kakao.maps.CustomOverlay({
      position: parentInitialPosition,
      content: overlayContent,
      yAnchor: 0.5,
      xAnchor: 0.5,
      zIndex: 1,
    });
    parentMarkerInstance.setMap(newMap);
    parentMarkerRef.current = parentMarkerInstance;
    updateParentLocation(parentMarkerRef);
  };

  const initializeWebSocket = async () => {
    if (isWebSocketInitialized.current) {
      return;
    }
    isWebSocketInitialized.current = true;

    const kindergartenId =
      parentInfo?.child.kindergartenClass.kindergarten.kindergartenId;
    const wsUrl = `${import.meta.env.VITE_WEBSOCKET_URL}/${kindergartenId}`;
    const ws = new WebSocket(wsUrl);
    wsRef.current = ws;

    receiveBusLocation(
      wsRef,
      setLocation,
      mapRef,
      busMarkerRef,
      setIsMoving,
      busCenterFlag,
      setBusOption
    );

    ws.onclose = () => {
      isWebSocketInitialized.current = false;
      centerFlag = false;
      busCenterFlag.current = false;
      setIsMoving(false);
    };

    ws.onerror = (error) => {
      console.error("WebSocket error:", error);
      setIsMoving(false);
    };
  };

  useEffect(() => {
    const apiKey = import.meta.env.VITE_KAKAO_API_KEY;

    const script = document.createElement("script");
    script.async = true;
    script.src = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${apiKey}&autoload=false`;
    document.head.appendChild(script);

    script.onload = () => {
      window.kakao.maps.load(() => {
        initializeMap();
        initializeWebSocket();
      });
    };

    const fetchBoardingStatus = async () => {
      setLoading(true);
      try {
        const fetchedParentInfo = await getParentInfo();
        setParentInfo(fetchedParentInfo);
        const currentChildId = fetchedParentInfo.child.childId;
        setChildId(currentChildId);

        const response = await getKidBoardingStatus(currentChildId);
        if (response) {
          setIsBoarding(response.status === "T");
        }
      } catch (error) {
        console.error(error);
      } finally {
        setLoading(false);
      }
    };

    fetchBoardingStatus();

    return () => {
      if (wsRef.current) {
        wsRef.current.close();
      }
      if (intervalIdRef.current) {
        clearInterval(intervalIdRef.current);
      }
    };
  }, []);

  const updateParentLocation = (markerRef: React.MutableRefObject<any>) => {
    centerFlag = false;
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords;
        const parentPosition = new window.kakao.maps.LatLng(
          latitude,
          longitude
        );

        const parentMarker = markerRef.current;
        parentMarker.setPosition(parentPosition);

        setParentLocation({ latitude, longitude });

        if (intervalIdRef.current) {
          clearInterval(intervalIdRef.current);
        }
        intervalIdRef.current = setInterval(() => {
          navigator.geolocation.getCurrentPosition(
            (pos) => {
              const { latitude, longitude } = pos.coords;
              const newParentPosition = new window.kakao.maps.LatLng(
                latitude,
                longitude
              );
              parentMarker.setPosition(newParentPosition);
              setParentLocation({ latitude, longitude });
              const map = mapRef.current;

              if (
                map &&
                !isMoving &&
                !centerFlag &&
                latitude !== undefined &&
                longitude !== undefined
              ) {
                map.setCenter(newParentPosition);
                centerFlag = true;
              }
            },
            (err) => {
              console.error("Error getting location", err);
            },
            {
              enableHighAccuracy: true,
              timeout: 20000,
              maximumAge: 0,
            }
          );
        }, 500);
      },
      (error) => {
        console.error("Error getting location", error);
      },
      {
        enableHighAccuracy: true,
        timeout: 20000,
        maximumAge: 0,
      }
    );
  };

  const handleBoardingStatus = async () => {
    try {
      await postKidBoardingStatus(childId);
      const response = await getKidBoardingStatus(childId);
      if (response) {
        setIsBoarding(response.status === "T");
      }
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const handleToggleChange = async () => {
    if (loading) {
      return <LoadingSpinner />;
    }
    const newStatus = !isBoarding;
    setIsBoarding(newStatus);
    await handleBoardingStatus();
  };

  const animateMapToMarker = (map: any, marker: any) => {
    if (marker) {
      const markerPosition = marker.getPosition();
      map.panTo(markerPosition);
    }
  };

  const currentTime = new Date().getHours();
  const main1 = isWebSocketInitialized.current
    ? currentTime < 12
      ? isMoving
        ? "등원 중"
        : "정차 중"
      : isMoving
      ? "하원 중"
      : "정차 중"
    : "정차 중";

  // main1이 "정차 중"이면 description1은 "버스가", 그렇지 않으면 "아이가"
  const description1 = main1 === "정차 중" ? "버스가" : "아이가";
  const main2 = "이에요";

  return (
    <div className="flex flex-col h-screen bg-[#FFEC8A]">
      <InfoSection
        description1={description1}
        main1={main1}
        main2={main2}
        imageSrc={daramgi}
        altText="다람쥐"
      />
      <div className="flex flex-col flex-grow overflow-hidden rounded-tl-[20px] rounded-tr-[20px] bg-white shadow-top animate-slideUp">
        <div className="flex flex-row items-center space-x-4">
          <Toggle isOn={isBoarding} toggleHandler={handleToggleChange} />
        </div>
        <div
          ref={mapContainer}
          className="w-full h-full relative z-0 mt-4"
        ></div>
      </div>

      <div className="fixed flex justify-end items-center bottom-20 right-0 gap-4 mr-4">
        <button
          onClick={() =>
            animateMapToMarker(mapRef.current, busMarkerRef.current)
          }
          className="relative bg-white text-yellow-500 p-2 rounded z-40 rounded-full drop-shadow-lg"
        >
          <FaBus />
        </button>
        <button
          onClick={() =>
            animateMapToMarker(mapRef.current, parentMarkerRef.current)
          }
          className="relative bg-white text-red-500 p-2 rounded z-40 rounded-full drop-shadow-lg"
        >
          <MdGpsFixed />
        </button>
      </div>
    </div>
  );
}
