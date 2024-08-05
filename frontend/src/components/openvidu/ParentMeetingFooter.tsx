import React from "react";
import { IoMic, IoMicOff, IoVideocam, IoVideocamOff } from "react-icons/io5";
import { ImExit } from "react-icons/im";

// Define the types for the props
interface ControlState {
  video: boolean;
  mic: boolean;
  muted: boolean;
  volume: number;
}

interface MeetingFooterProps {
  control: ControlState;
  handleControl: (update: (prev: ControlState) => ControlState) => void;
  close: () => void;
}

const ParentMeetingFooter: React.FC<MeetingFooterProps> = ({ control, handleControl, close }) => {
  return (
    <div className="fixed flex items-center jusstify-center w-full h-[100px] bottom-0 bg-transparent flex px-10 py-3 text-white rounded-full z-50 gap-4">
      <div className="flex gap-6">
        {control.video ? (
          <IoVideocam
            className="cursor-pointer text-4xl text-white"
            onClick={() => handleControl((prev) => ({ ...prev, video: false }))}
          />
        ) : (
          <IoVideocamOff
            className="cursor-pointer text-4xl text-[#B8B8B8]"
            onClick={() => handleControl((prev) => ({ ...prev, video: true }))}
          />
        )}
        {control.mic ? (
          <IoMic
            className="cursor-pointer text-4xl text-white"
            onClick={() => handleControl((prev) => ({ ...prev, mic: false }))}
          />
        ) : (
          <IoMicOff
            className="cursor-pointer text-4xl text-[#B8B8B8]"
            onClick={() => handleControl((prev) => ({ ...prev, mic: true }))}
          />
        )}
      </div>
      <div className="flex-grow"></div>
      <div className="flex flex-col justify-center items-center gap-2">
        <ImExit
          className="text-4xl cursor-pointer text-white"
          onClick={close}
        />
      </div>
    </div>
  );
};

export default ParentMeetingFooter;
