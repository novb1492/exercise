package reservationChart;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import common.commonService;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import reservation.reservationService;

public class reservationChartController implements Initializable {
	private Parent reservationChartForm;
	private showReservationChart showReservationChart;
	private reservationService reservationService;
	
	public void setChartForm(Parent reservationChartForm) {
		this.reservationChartForm=reservationChartForm;
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.showReservationChart=new showReservationChart();
		this.reservationService=new reservationService();
		
	}
	public void minusYear() {
		System.out.println("minusYear");
		Label labelYear=(Label) reservationChartForm.lookup("#yearLabel");
		showReservationChart.showReservationCharts(getYear(Integer.parseInt(labelYear.getText())));
		reservationService.closeWindow(reservationChartForm);
	}
	public void plusYear() {
		System.out.println("plusYear");
		Label labelYear=(Label) reservationChartForm.lookup("#yearLabel");
		int year=Integer.parseInt(labelYear.getText());
		if(year<LocalDate.now().getYear()) 
			year++;
			showReservationChart.showReservationCharts(year);
			reservationService.closeWindow(reservationChartForm);
		}else {
			commonService.ErrorMsg("error","제일 최근 연도 입니다");
		}
		
	}
	private int getYear(int year) {
		System.out.println("getYearGap");
		LocalDate date = LocalDate.of(year,1,1);
		LocalDate today=LocalDate.now();
		return today.getYear()-(today.getYear()-date.getYear())-1;
	}

}
