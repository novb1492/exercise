package reservation;



import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import common.commonService;
import dto.MemberShipDTO;
import dto.RegisterDTO;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;
import main.Controller;
import main.MainClass;
import memberDao.MemberShipDAO;


public class reservationService  {
	
	private final int openTime=9;
	private final int closeTime=18;
	private final int maxOfDay=60;
	private final int maxPeopleByTime=6;
	private final int lastDayIdNumber=37;
	private final String error="error";
	private reservationDao reservationDao=new reservationDao();
	private LocalDate today=LocalDate.now();
	private LocalDateTime todayLocalDateTime=LocalDateTime.now(); 

	public void showDatePage(RegisterDTO member,int plusMonth) {
		System.out.println("showDatePage");
		FXMLLoader loader = new FXMLLoader(getClass().getResource("reservationPage2.fxml"));
		Parent reservationForm=loadPageAndGetParent(loader);
		today=today.plusMonths(plusMonth);

		getMonth(reservationForm,today);
		getDays(reservationForm,plusMonth);
		showId(reservationForm, member.getId());
		showMemberShip(reservationForm,member.getId());
		showStage(reservationForm, "reservationPage");
		
		reservationController reservationController=loader.getController();
		reservationController.setReservationForm(reservationForm, member);
		
	}
	private void showMemberShip(Parent reservationForm,String id) {
		System.out.println("showMemberShip");
		MemberShipDTO memberShipDTO=findMemberShip(id);
		System.out.println(memberShipDTO);
		if(memberShipDTO!=null) {
			Label memberShipName=(Label) reservationForm.lookup("#memberShipName");
			Label memberShipDate=(Label) reservationForm.lookup("#memberShipDate");
			memberShipName.setText(memberShipDTO.getMemberShipName());
			memberShipDate.setText(memberShipDTO.getMemberShipDate());
			showMemberShipCount(reservationForm, memberShipDTO.getMemberShipCount());
		}
	}
	private MemberShipDTO findMemberShip(String id) {
		System.out.println("findMemberShip");
		MemberShipDAO memberShipDAO=new MemberShipDAO();
		return memberShipDAO.selectMemberShipById(id);
	}
	private void showMemberShipCount(Parent reservationForm,int count) {
		Label memberShipCount=(Label) reservationForm.lookup("#memberShipCount");
		memberShipCount.setText(count+"");
		
	}
	private void showId(Parent root,String name) {
		System.out.println("showname");
		Label showEmail=(Label)root.lookup("#id");
		showEmail.setText(name);
	}
	public Parent loadPageAndGetParent(FXMLLoader loader) {
		System.out.println("loadPageAndGetParent");
		try {
		Parent	root = loader.load();
		return root;
		} catch (IOException e) {
			System.out.println("parent �ε��� �����߻�");
			e.printStackTrace();
		}
		return null;
	}
	private void getMonth(Parent reservationForm,LocalDate today) {
		System.out.println("getMonth");
		Label month=(Label) reservationForm.lookup("#month");
		month.setText(Integer.toString(today.getMonthValue()));
	}
	private void getDays(Parent reservationForm,int plusMonth) {
		System.out.println("getDays");
		YearMonth yearMonth=YearMonth.from(today);
		int lastDay=yearMonth.lengthOfMonth();
		int start=0;
		LocalDate date = LocalDate.of(today.getYear(),today.getMonthValue(),1);
		DayOfWeek dayOfWeek = date.getDayOfWeek();
		int temp=1;
		start=dayOfWeek.getValue();
		int endDayIdOfMonth=lastDay+start;
		
		for(int i=1;i<start;i++) {
			Button button=(Button) reservationForm.lookup("#day"+i);
			button.setText("x");
			button.setDisable(true);
		}
		for(int i=start;i<endDayIdOfMonth;i++) {
			Button button=(Button) reservationForm.lookup("#day"+i);
			button.setText(Integer.toString(temp));
			if(checkFullDay(stringToTimestamp(today.getMonthValue()+"",temp))||compareDate(stringToTimestamp(today.getMonthValue()+"", temp),LocalDateTime.now())) {
				System.out.println(i+"���� ������ �� á�ų� ���� �����Դϴ�");
				button.setDisable(true);
			}
			temp+=1;
		}
		if(endDayIdOfMonth<lastDayIdNumber) {
			for(int i=endDayIdOfMonth;i<=lastDayIdNumber;i++) {
				Button button=(Button) reservationForm.lookup("#day"+i);
				button.setText("x");
				button.setDisable(true);
			}
		}
	}

	private void checkCloseHour(Parent reservationForm) {
		System.out.println("checkCloseHour");
		if(todayLocalDateTime.getHour()>=closeTime) {
			System.out.println("������ ���� �Ǿ����ϴ�");
			Button button=(Button) reservationForm.lookup("#day"+todayLocalDateTime.getDayOfMonth());
			button.setDisable(true);
		}
	}
	public void showStage(Parent root,String pageName ) {
		System.out.println("showStage");
		Stage stage=new Stage(); 
		stage.setTitle(pageName);
		stage.setScene(new Scene(root));
		stage.show();
	}
	private boolean checkFullDay(Timestamp timestamp) {
		System.out.println("checkFullDay"+timestamp);
		List<reservationDto>array=findByDate(timestamp);
		System.out.println(array.size()+"���ళ��");
		if(array.size()>=maxOfDay) {
			return true;
		}
		return false;
	}
	private List<reservationDto> findByDate(Timestamp timestamp) {
		System.out.println("findByDate");
		return reservationDao.findAllByDate(timestamp);
	}
	private boolean compareDate(Timestamp timestamp,LocalDateTime localDateTime) {
		LocalDateTime loaDateTime2=timestamp.toLocalDateTime();
		
		if(localDateTime.getDayOfMonth()==loaDateTime2.getDayOfMonth()){
			return false;
		}
		else if(localDateTime.isAfter(loaDateTime2)){
           return true;
        }
        return false;
	}
	public void showTimePage(Parent reservationForm,int day,RegisterDTO member) {
		try {
			System.out.println("showTimePage"+day);
			String id=member.getId();
			if(findMemberShip(id)==null) {
				System.out.println("����� �Ⱓ�� ����Ǿ��ų� ������� �����ϴ�");
				commonService.ErrorMsg(error,"����� �Ⱓ�� ����Ǿ��ų� ������� �����ϴ�");
				return;
			}
			FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("showTimePage.fxml"));
			Parent setShowTimePageForm=loadPageAndGetParent(fxmlLoader);
		
			Label getReservationPageMonth=(Label) reservationForm.lookup("#month");
			System.out.println(getReservationPageMonth.getText()+"��");
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Label showSelectDate=(Label) setShowTimePageForm.lookup("#labelDate");
			showSelectDate.setText(sdf.format(stringToTimestamp(getReservationPageMonth.getText(), day)));
			
			getTimes(setShowTimePageForm,reservationForm,day);
			showStage(setShowTimePageForm,"showTimePage");
			
			reservationController reservationController=fxmlLoader.getController();
			reservationController.setShowTimePageForm(reservationForm,setShowTimePageForm,day,member);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	private void getTimes(Parent ShowTimePageForm,Parent reservationForm,int day) {
		System.out.println("getTimes"+day);
		LocalDateTime localDateTime=LocalDateTime.now();
		Label month=(Label)reservationForm.lookup("#month");
		
		for(int i=openTime;i<=closeTime;i++) {
			RadioButton radioButton=(RadioButton) ShowTimePageForm.lookup("#rdaio"+i);
			radioButton.setText(i+"��~"+(i+1)+"��");
			Label maxOfTime=(Label)ShowTimePageForm.lookup("#maxOfTime"+i);
			maxOfTime.setText(maxPeopleByTime+"");
			int alreadyPeople=compareTime(month, day,i);
			if(alreadyPeople==maxPeopleByTime) {
				radioButton.setDisable(true);
			}
			Label timeOfPeople=(Label)ShowTimePageForm.lookup("#timeOfPeople"+i);
			timeOfPeople.setText(alreadyPeople+"");
			if(day==localDateTime.getDayOfMonth()) {
				if(i<=localDateTime.getHour()) {
					radioButton.setDisable(true);
				}
			}
		}
	}
	private int compareTime(Label month,int day,int time) {
		System.out.println("compareTime");
		List<reservationDto>array= findByDate(stringToTimestamp(month.getText(),day));
		int temp=0;
		for(reservationDto r:array) {
			if(r.getTime()==time) {
				temp++;
			}
			if(temp==maxPeopleByTime) {
				System.out.println(time+"�ô� ����ο��� ��á���ϴ�");
				return temp;
			}
		}
		return temp;
	}
	public void insert(Parent reservationForm,Parent ShowTimePageForm,int day,RegisterDTO member) {
		try {
			String id=member.getId();
			
			MemberShipDAO memberShipDAO=new MemberShipDAO();
			MemberShipDTO memberShipDTO=memberShipDAO.selectMemberShipById(id);
		
			Label month=(Label)reservationForm.lookup("#month");
			int time=getSelectTime(ShowTimePageForm);
		
			LocalDateTime reservationDate=Timestamp.valueOf(todayLocalDateTime.getYear()+"-"+month.getText()+"-"+day+" 00:00:00").toLocalDateTime();
			
			System.out.println(reservationDate+"����õ���");
			List<reservationDto>reservationDtos=reservationDao.findByRdate(Timestamp.valueOf(todayLocalDateTime.getYear()+"-"+month.getText()+"-"+day+" 00:00:00"));
			if(reservationDtos!=null) {
				for(reservationDto r:reservationDtos) {
					if(id.equals(r.getEmail())&&r.getTime()==time) {
						System.out.println("�̹� ���� �Ͻ� ���� �ð��Դϴ�");
						commonService.ErrorMsg(error,"�̹� ���� �Ͻ� ���� �ð��Դϴ�");
						return;
					}
				}
			}
			if(todayLocalDateTime.isAfter(Timestamp.valueOf(memberShipDTO.getMemberShipDate()+" 00:00:00").toLocalDateTime())) {
				System.out.println("����� �Ⱓ�� ����Ǿ����ϴ�");
				commonService.ErrorMsg(error, "����� �Ⱓ�� ����Ǿ����ϴ�");
				return;
			}
			if(reservationDate.isAfter(Timestamp.valueOf(memberShipDTO.getMemberShipDate()+" 00:00:00").toLocalDateTime())) {
				System.out.println("����� �Ⱓ�� ���ڸ� ������ �ּ���");
				commonService.ErrorMsg(error, "����� �Ⱓ�� ���ڸ� ������ �ּ���");
				return;
			}
			/*if(memberShipDTO.getMemberShipCount()<1) {
				System.out.println("���� Ƚ���� ���� �Ͽ����ϴ�");
				commonService.ErrorMsg(error, "���� Ƚ���� ���� �Ͽ����ϴ�");
				return;
			}*/
			if(day==0||day>31) {
				System.out.println("�߸��� ��¥ �����Դϴ�");
				commonService.ErrorMsg(error,"�߸��� ��¥ �����Դϴ�");
				return;
			}
			if(checkFullDay(stringToTimestamp(month.getText(),day))||compareDate(stringToTimestamp(month.getText(), day),LocalDateTime.now())) {
				System.out.println(day+"���� ������ �� á�ų� ���� ���� ����õ� �Դϴ�");
				commonService.ErrorMsg(error, "���� ������ �� á�ų� ���� ���� ����õ� �Դϴ�");
				return;
			}
			if(compareTime(month,day,time)==6) {
				System.out.println(time+"�ô� ����ο��� ��á���ϴ�");
				commonService.ErrorMsg(error,time+"�ô� ����ο��� ��á���ϴ�");
				return;
			}
			if(todayLocalDateTime.getDayOfMonth()==day) {
				if(time<=todayLocalDateTime.getHour()) {
					System.out.println(time+"�ô� ���� �ð��Դϴ�");
					commonService.ErrorMsg(error,time+"�ô� ���� �ð��Դϴ�");
					return;
				}
			}
			if(id==null||id.isEmpty()) {
				System.out.println("ȸ�������� ���� ���� �õ� �Դϴ�");
				commonService.ErrorMsg(error, "ȸ�������� ���� ���� �õ� �Դϴ�");
				return;
			}
			if(month.getText().isEmpty()||month.getText()==null||time==0) {
				System.out.println("���� ������ ���� ���� �õ��Դϴ�");
				commonService.ErrorMsg(error, "���� ������ ���� ���� �õ��Դϴ�");
				return;
			}

			System.out.println("�����"+month.getText());
			System.out.println("������"+day);
		
			Timestamp rDate=stringToTimestamp(month.getText(),day);
			System.out.println("������� "+rDate);
			
			Timestamp created=Timestamp.valueOf(todayLocalDateTime);
			System.out.println("�������� "+created);
			
			Timestamp dateAndTime=Timestamp.valueOf(todayLocalDateTime.getYear()+"-"+month.getText()+"-"+day+" "+time+":00:00");
			System.out.println("�������� +�ð�"+dateAndTime);
			
			reservationDto reservationDto=new reservationDto();
			reservationDto.setEmail(id);
			reservationDto.setName(member.getName());
			reservationDto.setCreated(created);
			reservationDto.setTime(time);
			reservationDto.setrDate(rDate);
			reservationDto.setDateAndTime(dateAndTime);
			reservationDto.setYear(LocalDate.now().getYear());
			reservationDao.insert(reservationDto);
			System.out.println("���༺��");
			closeWindow(ShowTimePageForm);
			int count=memberShipDTO.getMemberShipCount();
			memberShipDAO.updateMemberShipCount(id,count-=1);
			showMemberShipCount(reservationForm, count);
		} catch (Exception e) {
			System.out.println("������ ���� �߻�");
			commonService.ErrorMsg(error, "������ ���� �߻�");
			e.printStackTrace();
		}
			
	}
	private int getSelectTime(Parent ShowTimePageForm) {
		List<RadioButton>array=new ArrayList<RadioButton>();
		int time=0;
		for(int i=openTime;i<=closeTime;i++) {
			array.add((RadioButton)ShowTimePageForm.lookup("#rdaio"+i));
		}
		for(RadioButton r:array) {
			if(r.isSelected()) {
				String sTime=r.getText();
				time=Integer.parseInt(sTime.split("~")[0].replace("��",""));
			}
		}
		System.out.println("��� �ð� "+time);
		return time;
	}
	private Timestamp stringToTimestamp(String month,int day) {
		return Timestamp.valueOf(today.getYear()+"-"+month+"-"+day+" 00:00:00");
	}
	public void closeWindow(Parent parent) {
		System.out.println("closeWindow");
		Stage stage=(Stage) parent.getScene().getWindow();
		stage.close();
	}
}
