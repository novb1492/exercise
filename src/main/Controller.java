package main;

import java.net.URL;

import java.util.ResourceBundle;

import common.commonService;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import login.LoginService;
import register.RegisterService;




public class Controller implements Initializable{
	private Parent loginForm;
	private Parent registerForm;

	
	private LoginService loginSvc;
	private RegisterService regSvc;

	
	public void setLoginForm(Parent loginForm) {
		this.loginForm=loginForm;
	}
	public void setRegisterForm(Parent registerForm) {
		this.registerForm = registerForm;
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		loginSvc = new LoginService();
		regSvc = new RegisterService();

	}
	
	public void LoginProc() {
		loginSvc.LoginProc(loginForm);
	}
	public void RegisterOpenProc() {
		loginSvc.RegisterOpenProc();
	}
	public void RegisterProc() {
		regSvc.RegisterProc(registerForm);
	}
	public void CancelProc(ActionEvent event) {
		commonService.windowClose(event);
	}
	public void adminOpenProc() {
		loginSvc.adminOpenProc();
	}
	
}
