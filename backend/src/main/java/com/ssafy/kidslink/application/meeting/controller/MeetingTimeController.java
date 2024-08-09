package com.ssafy.kidslink.application.meeting.controller;

import com.ssafy.kidslink.application.meeting.domain.MeetingSchedule;
import com.ssafy.kidslink.application.meeting.dto.*;
import com.ssafy.kidslink.application.meeting.service.MeetingTimeService;
import com.ssafy.kidslink.common.dto.APIError;
import com.ssafy.kidslink.common.dto.APIResponse;
import com.ssafy.kidslink.common.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meeting")
@RequiredArgsConstructor
@Slf4j
public class MeetingTimeController {
    private final MeetingTimeService meetingTimeService;


    @Operation(summary = "상담 일정 오픈", description = "상담 일정을 오픈합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "상담 일정을 오픈하기 위한 요청 데이터",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = OpenMeetingTimeDTO.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상담 일정 오픈 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효한 JWT 토큰이 필요합니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)))
    })
    @PostMapping("/open")
    public ResponseEntity<APIResponse<Void>> openMeetingTimes(@AuthenticationPrincipal Object principal, @RequestBody List<OpenMeetingTimeDTO> requestDTOs){
        if(principal instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) principal;

            meetingTimeService.openMeetingTimes(userDetails.getUsername(), requestDTOs);
            APIResponse<Void> responseData = new APIResponse<>(
                    "success",
                    null,
                    "상담 일정 오픈에 성공하였습니다.",
                    null
            );
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        }
        APIError apiError = new APIError("UNAUTHORIZED", "유효한 JWT 토큰이 필요합니다.");

        APIResponse<Void> responseData = new APIResponse<>(
                "success",
                null,
                "상담 일정 오픈을 실패했습니다.",
                apiError
        );

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }



    @Operation(summary = "상담 가능 일정 조회", description = "사용자의 상담 가능한 일정을 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상담 일정 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효한 JWT 토큰이 필요합니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)))
    })
    @GetMapping("")
    public ResponseEntity<APIResponse<List<MeetingTimeDTO>>> getMeetingTimes(@AuthenticationPrincipal Object principal){
        if(principal instanceof CustomUserDetails userDetails){
            List<MeetingTimeDTO> meetingTimes = meetingTimeService.getMeetingTimes(userDetails);
            APIResponse<List<MeetingTimeDTO>> responseData = new APIResponse<>(
                    "success",
                    meetingTimes,
                    "상담 일정 조회에 성공하였습니다.",
                    null
            );
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        }
        APIError apiError = new APIError("UNAUTHORIZED", "유효한 JWT 토큰이 필요합니다.");

        APIResponse<List<MeetingTimeDTO>> responseData = new APIResponse<>(
                "success",
                null,
                "상담 일정 조회에 실패했습니다.",
                apiError
        );

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);

    }



    @Operation(summary = "상담 일정 예약", description = "상담 일정을 예약합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string")),
                    @Parameter(name = "id", description = "상담 일정 ID", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer"))
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "상담 일정 예약을 위한 요청 데이터",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ReserveMeetingDTO.class))
            ))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상담 일정 예약 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효한 JWT 토큰이 필요합니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)))
    })
    @PostMapping("")
    public ResponseEntity<APIResponse<Void>> selectMeeting(@AuthenticationPrincipal Object principal, @RequestBody List<ReserveMeetingDTO> requestDTOs){
        if(principal instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) principal;

            meetingTimeService.selectMeeting(userDetails.getUsername(), requestDTOs);
            APIResponse<Void> responseData = new APIResponse<>(
                    "success",
                    null,
                    "상담 일정 선택에 성공하였습니다.",
                    null
            );
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        }
        APIError apiError = new APIError("UNAUTHORIZED", "유효한 JWT 토큰이 필요합니다.");

        APIResponse<Void> responseData = new APIResponse<>(
                "fail",
                null,
                "상담 일정 선택을 실패했습니다.",
                apiError
        );

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }



    @Operation(summary = "상담 예약 일정 조회", description = "사용자의 상담 예약 일정을 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT 토큰", required = true, in = ParameterIn.HEADER, schema = @Schema(type = "string"))
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상담 예약 일정 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효한 JWT 토큰이 필요합니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = APIResponse.class)))
    })
    @GetMapping("/reservation")
    public ResponseEntity<APIResponse<List<MeetingScheduleDTO>>> getMeetingReservations(@AuthenticationPrincipal Object principal){
        if(principal instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            String role = userDetails.getAuthorities().iterator().next().getAuthority();

            List<MeetingScheduleDTO> meetingReservations = meetingTimeService.getMeetingReservations(role,userDetails.getUsername());
            APIResponse<List<MeetingScheduleDTO>> responseData = new APIResponse<>(
                    "success",
                    meetingReservations,
                    "상담 예약 일정 조회에 성공하였습니다.",
                    null
            );
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        }
        APIError apiError = new APIError("UNAUTHORIZED", "유효한 JWT 토큰이 필요합니다.");

        APIResponse<List<MeetingScheduleDTO>> responseData = new APIResponse<>(
                "success",
                null,
                "상담 예약 일정 조회에 실패했습니다.",
                apiError
        );

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/classify")
    public ResponseEntity<APIResponse<List<SelectedMeetingDTO>>> classifySchedule(@AuthenticationPrincipal Object principal,@RequestBody List<SelectedMeetingDTO> selectedMeetingDTOS){
        if(principal instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) principal;

            List<SelectedMeetingDTO> meetings = meetingTimeService.classifySchedule(selectedMeetingDTOS);
            meetingTimeService.deleteMeeting(userDetails.getUsername());
            meetingTimeService.deleteMeetingTime(userDetails.getUsername());
            APIResponse<List<SelectedMeetingDTO>> responseData = new APIResponse<>(
                    "success",
                    meetings,
                    "상담 일정 분류에 성공하였습니다.",
                    null
            );
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        }
        APIError apiError = new APIError("UNAUTHORIZED", "유효한 JWT 토큰이 필요합니다.");

        APIResponse<List<SelectedMeetingDTO>> responseData = new APIResponse<>(
                "fail",
                null,
                "상담 일정 분류에 실패했습니다.",
                apiError
        );

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

//    @PostMapping("/confirm")
//    public ResponseEntity<APIResponse<List<MeetingRoomDTO>>> confirmMeeting(@AuthenticationPrincipal Object principal){
//        if(principal instanceof CustomUserDetails){
//            CustomUserDetails userDetails = (CustomUserDetails) principal;
//
//            List<MeetingRoomDTO> meetings = meetingTimeService.confirmMeeting(userDetails.getUsername());
//            meetingTimeService.deleteMeeting(userDetails.getUsername());
//            meetingTimeService.deleteMeetingTime(userDetails.getUsername());
//            APIResponse<List<MeetingRoomDTO>> responseData = new APIResponse<>(
//                    "success",
//                    meetings,
//                    "상담 일정 확정에 성공하였습니다.",
//                    null
//            );
//            return new ResponseEntity<>(responseData, HttpStatus.OK);
//        }
//        APIError apiError = new APIError("UNAUTHORIZED", "유효한 JWT 토큰이 필요합니다.");
//
//        APIResponse<List<MeetingRoomDTO>> responseData = new APIResponse<>(
//                "fail",
//                null,
//                "상담 일정 확정을 실패했습니다.",
//                apiError
//        );
//
//        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
//    }

    @GetMapping("/selected")
    public ResponseEntity<APIResponse<List<SelectedMeetingDTO>>> getSelectedMeetings(@AuthenticationPrincipal Object principal){
        if(principal instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            List<SelectedMeetingDTO> meetings = meetingTimeService.getSelectedMeetings(userDetails.getUsername());

            APIResponse<List<SelectedMeetingDTO>> responseData = new APIResponse<>(
                    "success",
                    meetings,
                    "부모가 선택한 모든 상담 일정 조회에 성공하였습니다.",
                    null
            );
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        }
        APIError apiError = new APIError("UNAUTHORIZED", "유효한 JWT 토큰이 필요합니다.");

        APIResponse<List<SelectedMeetingDTO>> responseData = new APIResponse<>(
                "fail",
                null,
                "부모가 선택한 모든 상담 일정 조회에 실패했습니다.",
                apiError
        );

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);


    }

    @GetMapping("/{meetingId}")
    public ResponseEntity<APIResponse<MeetingRoomDTO>> enterMeeting(@AuthenticationPrincipal Object principal,
                                                                    @Parameter(description = "상담 ID") @PathVariable int meetingId){
        if(principal instanceof CustomUserDetails){
            CustomUserDetails userDetails = (CustomUserDetails) principal;

            MeetingRoomDTO details = meetingTimeService.enterMeeting(meetingId);
            APIResponse<MeetingRoomDTO> responseData = new APIResponse<>(
                    "success",
                    details,
                    "화상 상담 입장에 성공하였습니다.",
                    null
            );
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        }
        APIError apiError = new APIError("UNAUTHORIZED", "유효한 JWT 토큰이 필요합니다.");

        APIResponse<MeetingRoomDTO> responseData = new APIResponse<>(
                "fail",
                null,
                "화상 상담 입장을 실패했습니다.",
                apiError
        );

        return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
    }

}

