package reservationChart;


import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import main.Controller;
import reservation.reservationDao;
import reservation.reservationDto;
import reservation.reservationService;

public class showReservationChart {
	
	private final int openTime=9;
	private final int closeTime=18;
	private reservationService reservationService=new reservationService();
	private reservationDao reservationDao=new reservationDao();
	
	
	public void showReservationCharts(int year) {
		System.out.println("showReservationCharts");
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("reservationChartPage.fxml"));
			Parent reservationChartForm=reservationService.loadPageAndGetParent(loader);
			Label yearLabel=(Label) reservationChartForm.lookup("#yearLabel");
			yearLabel.setText(year+"");
			
			showRankOfTime(reservationChartForm,year);
			showRankOfMonth(reservationChartForm,year);
			showRankOfDay(reservationChartForm,year);
			reservationService.showStage(reservationChartForm,"reservationChartPage");
			
			reservationChartController reservationChartController=loader.getController();
			reservationChartController.setChartForm(reservationChartForm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void showRankOfDay(Parent reservationChartForm,int year) {
		System.out.println("showRankOfDay");
		int[] daysValues=makeIntValues(31, 1, 31, "day",year);
		Map<Integer, Integer>countByDay=new HashMap<Integer, Integer>();
		for(int i=1;i<=31;i++) {
			countByDay.put(i,daysValues[i-1]);
		}
		makeChart(reservationChartForm, "#dayChart", countByDay, 1, 31, "일", "일별 예약자 수");
	}
	private void showRankOfMonth(Parent reservationChartForm,int year) {
		System.out.println("showRankOfMonth");
		int[] monthValues= makeIntValues(12, 1, 12, "month",year);
		Map<Integer, Integer>countByMonth=new HashMap<Integer, Integer>();
		for(int i=1;i<=12;i++) {
			countByMonth.put(i,monthValues[i-1]);
		}
		
		makeChart(reservationChartForm, "#monthChart", countByMonth, 1, 12, "월", "월별 예약자 수");
	}
	private void showRankOfTime(Parent reservationChartForm,int year) {
		System.out.println("showRankOfTime");
		try {
			int[] timeValues= makeIntValues(10, openTime, closeTime,"time",year);
			Map<Integer, Integer>countByTime=new HashMap<Integer, Integer>();
			int temp=0;
			for(int i=openTime;i<=closeTime;i++) {
				countByTime.put(i, timeValues[temp]);
				temp+=1;
			}
			/*List<Integer> countByTimeKeys = sortMapKeyByValue(countByTime);
			System.out.println("시간 빈도 대로 정렬 내용");
			for(Integer key : countByTimeKeys) {
	            System.out.println(String.format("시간 :"+key+" 총예약회수 :"+ countByTime.get(key)));
	        }*/
			makeChart(reservationChartForm, "#timeChart", countByTime,openTime, closeTime, "시", "시간별 예약 수");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private int[] makeIntValues(int totalCountOfUnit,int start,int end,String unit,int year) {
		System.out.println("makeIntValues");
		List<reservationDto>allRerservations=reservationDao.findByYear(year);
		int[] Values= new int[totalCountOfUnit];
		for(reservationDto r:allRerservations) {
			if(unit.equals("time")) {
				for(int i=start;i<=end;i++) {
					if(r.getTime()==i) {
						Values[i-start]+=1;
						break;
					}
				}
			}else if(unit.equals("month")) {
				int month=spliteTimestamp(r.getrDate(),1);
				for(int i=start;i<=end;i++) {
					if(month==i) {
						Values[i-start]+=1;
						break;
					}
				}
			}else if(unit.equals("day")) {
				int day=spliteTimestamp(r.getrDate(),2);
				for(int i=start;i<=end;i++) {
					if(day==i) {
						Values[i-start]+=1;
						break;
					}
				}
			}
		}
		return Values;
	}
	private int spliteTimestamp(Timestamp timestamp,int num) {
		System.out.println("spliteTimestamp");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sdate=sdf.format(timestamp);
		String[] splite=sdate.split("-");
		return Integer.parseInt(splite[num]);
	}
	private void makeChart(Parent reservationChartForm,String chartId,Map<Integer, Integer>countBy,int start,int end,String unit,String chartName) {
		System.out.println("makeChart");
		BarChart<String, Integer>Chart=(BarChart<String, Integer>) reservationChartForm.lookup(chartId);
		XYChart.Series<String, Integer> series = new XYChart.Series<>();
		series.setName(chartName);
		for (int i = start; i <= end; i++) {
        	series.getData().add(new XYChart.Data<>(i+unit,countBy.get(i)));
      }
		Chart.getData().add(series);
	}
	private List<Integer> sortMapKeyByValue(Map<Integer, Integer>map) {
		System.out.println("sortMapKeyByValue");
		List<Integer> array = new ArrayList<>(map.keySet());
		   Collections.sort(array, new Comparator<Integer>() {
				@Override
				public int compare(Integer o1, Integer o2) {
					  return map.get(o2).compareTo(map.get(o1));
				}
		   	});
		   return array;
	} 
}
