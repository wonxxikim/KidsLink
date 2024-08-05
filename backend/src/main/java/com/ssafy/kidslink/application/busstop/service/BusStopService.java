package com.ssafy.kidslink.application.busstop.service;

import com.ssafy.kidslink.application.bus.domain.Bus;
import com.ssafy.kidslink.application.bus.dto.BusStopDTO;
import com.ssafy.kidslink.application.bus.repository.BusRepository;
import com.ssafy.kidslink.application.busstop.domain.BusStop;
import com.ssafy.kidslink.application.busstop.repository.BusStopRepository;
import com.ssafy.kidslink.application.busstopchild.domain.BusStopChild;
import com.ssafy.kidslink.application.busstopchild.dto.BusStopChildDTO;
import com.ssafy.kidslink.application.busstopchild.repository.BusStopChildRepository;
import com.ssafy.kidslink.application.child.domain.Child;
import com.ssafy.kidslink.application.child.repository.ChildRepository;
import com.ssafy.kidslink.application.kindergarten.domain.Kindergarten;
import com.ssafy.kidslink.application.kindergarten.domain.KindergartenClass;
import com.ssafy.kidslink.application.kindergarten.repository.KindergartenClassRepository;
import com.ssafy.kidslink.application.kindergarten.repository.KindergartenRepository;
import com.ssafy.kidslink.application.notification.domain.ParentNotification;
import com.ssafy.kidslink.application.notification.respository.ParentNotificationRepository;
import com.ssafy.kidslink.application.notification.respository.TeacherNotificationRepository;
import com.ssafy.kidslink.application.parent.domain.Parent;
import com.ssafy.kidslink.application.parent.repository.ParentRepository;
import com.ssafy.kidslink.application.teacher.domain.Teacher;
import com.ssafy.kidslink.application.teacher.repository.TeacherRepository;
import com.ssafy.kidslink.common.enums.BoardingStatus;
import com.ssafy.kidslink.common.enums.NotificationCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusStopService {
    private final BusStopRepository busStopRepository;
    private final BusStopChildRepository busStopChildRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final TeacherNotificationRepository teacherNotificationRepository;
    private final ParentNotificationRepository parentNotificationRepository;
    private final KindergartenClassRepository kindergartenClassRepository;
    private final ChildRepository childRepository;
    private final KindergartenRepository kindergartenRepository;
    private final BusRepository busRepository;

    public List<BusStop> getAllBusStops() {
        return busStopRepository.findAll();
    }

    public List<BusStopChildDTO> getBusStopChildren(int busStopId) {
        List<BusStopChild> busStopChildList = busStopChildRepository.findByBusStopId(busStopId);
        List<BusStopChildDTO> busStopChildren = new ArrayList<>();

        for(BusStopChild busStopChild : busStopChildList.stream().toList()){
            BusStopChildDTO busStopChildDTO = new BusStopChildDTO();

            busStopChildDTO.setChildName(busStopChild.getChild().getChildName());
            busStopChildDTO.setParentTel(busStopChild.getChild().getParent().getParentTel());
            busStopChildDTO.setStatus(busStopChild.getBusBoardingStatus());

            busStopChildren.add(busStopChildDTO);
        }

         return busStopChildren;

    }

    public void isBoarding(int childId){
        Child child = childRepository.findById(childId).get();
        if (busStopChildRepository.findByChild(child).iterator().next().getBusBoardingStatus() == BoardingStatus.F) {
            busStopChildRepository.updateBoardingStatus(childId, BoardingStatus.T);
        } else {
            busStopChildRepository.updateBoardingStatus(childId, BoardingStatus.F);
        }
    }

    public BusStopChildDTO getBusStopChild(String Username){
        Parent parent = parentRepository.findByParentUsername(Username);
        Child child = parent.getChildren().iterator().next();

        BusStopChild busStopChild = busStopChildRepository.findByChild(child).iterator().next();

        BusStopChildDTO busStopChildDTO = new BusStopChildDTO();
        busStopChildDTO.setChildName(busStopChild.getChild().getChildName());
        busStopChildDTO.setStatus(busStopChild.getBusBoardingStatus());
        busStopChildDTO.setParentTel(busStopChild.getChild().getParent().getParentTel());

        return busStopChildDTO;
    }

    public void sendBusNotification(String teacherUsername) {
        Teacher teacher = teacherRepository.findByTeacherUsername(teacherUsername);
        Kindergarten kindergarten = teacher.getKindergartenClass().getKindergarten();
        for (KindergartenClass select : kindergartenClassRepository.findByKindergarten(kindergarten))
            for (Parent parent : parentRepository.findByKindergartenClassId(select.getKindergartenClassId())) {
                ParentNotification parentNotification = new ParentNotification();
                parentNotification.setCode(NotificationCode.BUS);
                parentNotification.setParentNotificationText("버스가 출발하였습니다.");
                parentNotification.setParentNotificationDate(LocalDate.now());
                parentNotification.setParent(parent);
                parentNotificationRepository.save(parentNotification);
            }
    }

    public List<BusStopDTO> getAllBusStopFromKindergarten(int kindergartenId) {
        Kindergarten kindergarten = kindergartenRepository.findById(kindergartenId).orElseThrow();

        Bus bus = busRepository.findByKindergarten(kindergarten);
        List<BusStopDTO> busStopDTOList = new ArrayList<>();
        List<BusStop> busStops = new ArrayList<>(bus.getBusStops());

        busStops = busStops.stream()
                .sorted((a, b) -> Integer.compare(a.getBusStopId(), b.getBusStopId()))
                .toList();

        for (int i = 0; i < busStops.size(); i++) {
            BusStopDTO busStopDTO = new BusStopDTO();
            busStops.get(i).setStopOrder(i + 1);
            busStopDTO.setBusId(bus.getBusId());
            int busStopId = busStops.get(i).getBusStopId();
            busStopDTO.setBusStopId(busStopId);
            busStopDTO.setBusStopName(busStops.get(i).getBusStopName());
            List<BusStopChildDTO> busStopChildren = getBusStopChildren(busStopId);
            busStopDTO.setChildren(busStopChildren);
            busStopDTOList.add(busStopDTO);
        }
        return busStopDTOList;
    }
}
