package register;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import common.commonService;
import dto.MemberShipDTO;
import dto.RegisterDTO;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import memberDao.MemberDAO;
import memberDao.MemberShipDAO;

public class RegisterService {

	public void RegisterProc(Parent registerForm) {
		RegisterDTO red = new RegisterDTO();
		MemberShipDTO mDto = new MemberShipDTO();
		
		
		TextField nameText = (TextField)registerForm.lookup("#nameText");
		TextField idText = (TextField)registerForm.lookup("#idText");
		PasswordField pwText = (PasswordField)registerForm.lookup("#pwText");
		PasswordField confirmText = (PasswordField)registerForm.lookup("#confirmText");
		TextField phoneText = (TextField)registerForm.lookup("#phoneText");
		TextField age = (TextField)registerForm.lookup("#ageText");
		RadioButton man = (RadioButton)registerForm.lookup("#manRadio");
		RadioButton woman = (RadioButton)registerForm.lookup("#womanRadio");
		TextField email = (TextField)registerForm.lookup("#emailText");
		String gender = "";
		if(man.isSelected())
			gender = "��";
		else if(woman.isSelected())
			gender = "��";
		else
			gender = "���� ����.";
		
			
		CheckBox naverCheck = (CheckBox)registerForm.lookup("#naverCheck");
		CheckBox instarCheck = (CheckBox)registerForm.lookup("#instarCheck");
		CheckBox nearCheck = (CheckBox)registerForm.lookup("#nearCheck");
		CheckBox adCheck = (CheckBox)registerForm.lookup("#adCheck");
		String checks = "";
		if(naverCheck.isSelected())
			checks += "���̹� ";
		if(instarCheck.isSelected())
			checks += "�ν�Ÿ ";
		if(nearCheck.isSelected())
			checks += "������õ";
		if(adCheck.isSelected())
			checks += "������";
		
		RadioButton registration = (RadioButton)registerForm.lookup("#registration");
		RadioButton unregistered = (RadioButton)registerForm.lookup("#unregistered");
		String enrollment = "";
		if(registration.isSelected()) {
			enrollment += "���";
		}else if(unregistered.isSelected()) {
			enrollment += "�̵��";
		}
		
		
		red.setName(nameText.getText());
		red.setId(idText.getText());
		red.setPw(pwText.getText());
		red.setGender(gender);
		red.setPhoneNumber(phoneText.getText());
		red.setAge(age.getText());
		red.setRoute(checks);
		red.setEmail(email.getText());
		red.setEnrollment(enrollment);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, +1);
		Date currentTime = cal.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String memberShipDate = formatter.format(currentTime);
		
		mDto.setMemberId(idText.getText());
		mDto.setMemberEmail(email.getText());
		mDto.setMemberShipDate(memberShipDate);
		mDto.setMemberShipCount(5);
		
		
		if(pwText.getText().equals(confirmText.getText() ) ){
			MemberDAO dao = new MemberDAO();
			MemberShipDAO mDao = new MemberShipDAO();
			RegisterDTO check = dao.selectId(idText.getText());
			if(check == null) {
				dao.insert(red);
				commonService.info("��� �Ϸ�.");
				if(enrollment.equals("���")) {
					mDao.insert(mDto);
				}
			}else {
				commonService.ErrorMsg("��ϵ� �����Դϴ�.");
			}
		}else {
			commonService.ErrorMsg("�Է��Ͻ� ��й�ȣ�� ��ġ���� �ʽ��ϴ�.");
		}

	}

}