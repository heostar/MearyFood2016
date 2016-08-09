package kr.sc.mearyfood2012;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.RemoteViews;

public class FoodWidget extends AppWidgetProvider {

	DownThread mThread;	
	final static String PREF = "kr.sc.mearyfood2012";
	Calendar cal = new GregorianCalendar();
	
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
		
		StringBuilder idbuilder = new StringBuilder();
		for(int i = 0; i < appWidgetIds.length; i++){
			if (appWidgetIds[i] != 0){
				UpdateMenus(context, appWidgetManager, appWidgetIds[i]);
				idbuilder.append("i"+appWidgetIds[i]+"e");
			}
		}
		
		//appWidgetIds 저장
		try{
			FileOutputStream fos = context.openFileOutput("appWidgetIds.txt", Context.MODE_WORLD_READABLE);
			fos.write(idbuilder.toString().getBytes());
			fos.close();
		}catch (Exception e){			
		}
		
	}
	
	public void UpdateMenus(Context context, AppWidgetManager appWidgetManager, int widgetId){
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetw);
				
		//안내문구표시//
		views.setTextViewText(R.id.menu1, "업데이트중...");
		views.setTextViewText(R.id.menu2, "최대소요시간10초");
		views.setTextViewText(R.id.menu3, "10초를 넘길 시");
		views.setTextViewText(R.id.menu4, "다시 업데이트하세요.");
		views.setTextViewText(R.id.menu5, " ");
		views.setTextViewText(R.id.menu6, " ");
		views.setTextViewText(R.id.menu7, " ");
		//안내문구표시완료//
		
			
		
		//배경화면 설정 시작
		SharedPreferences prefs = context.getSharedPreferences(PREF, 0);
		Long target = prefs.getLong("bG_" + widgetId, 0);
		changeBG(views,target);
		//배경화면 설정 완료
		
		//시간텍스트 설정
		StringBuilder time = new StringBuilder();
		Calendar cal = new GregorianCalendar();
		
		StringBuilder dayOfWeek = new StringBuilder();
		if (cal.get(Calendar.DAY_OF_WEEK) == 1){
			dayOfWeek.append("일요일");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 2){
			dayOfWeek.append("월요일");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 3){
			dayOfWeek.append("화요일");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 4){
			dayOfWeek.append("수요일");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 5){
			dayOfWeek.append("목요일");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 6){
			dayOfWeek.append("금요일");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 7){
			dayOfWeek.append("토요일");
		}
		
		time.append(String.format("%d년 %d월 %d일 " + dayOfWeek.toString(), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH)));
		views.setTextViewText(R.id.date, time.toString());
		//시간텍스트 설정 완료
		
		//수동업데이트 인텐트//
		Intent intentpop = new Intent(context, WidgetUpdateConfirm.class);
		intentpop.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		intentpop.setAction("kr.sc.mearyfood2012.ACTION_MANUAL_UPDATE");
		PendingIntent pending = PendingIntent.getActivity(context, widgetId, intentpop, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.bGimage, pending);
		//클릭시 WidgetUpdateConfirm Activity로 이동
				
		appWidgetManager.updateAppWidget(widgetId, views);
		
		//메뉴 설정 멀티스레드//
		mThread = new DownThread(context, widgetId, views);
		mThread.start();
		//다운스레드로 이동//
	}
	
	public void onDeleted(Context context, int[] appWidgetIds){
		context.deleteFile("widgetMode.txt");
		for (int i = 0; i < appWidgetIds.length; i++){
			SharedPreferences prefs = context.getSharedPreferences(PREF, 0);
			SharedPreferences.Editor editor = prefs.edit();
			editor.remove("bG_" + appWidgetIds[i]);
			editor.commit();
		}
	}
	
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action != null && action.equals("kr.sc.mearyfood2012.ACTION_MANUAL_UPDATE")){
			int id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			UpdateMenus(context, AppWidgetManager.getInstance(context), id);
			return;
		}
		super.onReceive(context, intent);
	}
	
	public static void changeTxtColor(RemoteViews viewy, int red, int green, int blue){
		viewy.setTextColor(R.id.date, Color.rgb(red, green, blue));
		viewy.setTextColor(R.id.menu1, Color.rgb(red, green, blue));
		viewy.setTextColor(R.id.menu2, Color.rgb(red, green, blue));
		viewy.setTextColor(R.id.menu3, Color.rgb(red, green, blue));
		viewy.setTextColor(R.id.menu4, Color.rgb(red, green, blue));
		viewy.setTextColor(R.id.menu5, Color.rgb(red, green, blue));
		viewy.setTextColor(R.id.menu6, Color.rgb(red, green, blue));
		viewy.setTextColor(R.id.menu7, Color.rgb(red, green, blue));
	}
	
	public static void changeBG(RemoteViews views, Long target){
		if (target == 0){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2ylw_100);
		}
		else if(target == 1){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2ylw_75);
		}
		else if(target == 2){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2ylw_50);
		}
		else if(target == 3){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2blk_100);
			changeTxtColor(views, 230, 230, 230);
		}
		else if(target == 4){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2blk_75);
			changeTxtColor(views, 230, 230, 230);
		}		
		else if(target == 5){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2blk_50);
			changeTxtColor(views, 230, 230, 230);
		}
		else if(target == 6){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2pnk_100);
		}
		else if(target == 7){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2pnk_75);
		}
		else if(target == 8){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2pnk_50);
		}
		else if(target == 9){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2wht_100);
		}
		else if(target == 10){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2wht_75);
		}
		else if(target == 11){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2wht_50);
		}
		else if(target == 12){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2prpl_100);
		}
		else if(target == 13){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2prpl_75);
		}
		else if(target == 14){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2prpl_50);
		}
		else if(target == 15){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2tpw);
			changeTxtColor(views, 255, 255, 255);
		}
		else if(target == 16){
			views.setImageViewResource(R.id.bGimage, R.drawable.frame2x2tpb);
		}
	}

	//이것이 멀티스레드//
	class DownThread extends Thread {
		StringBuilder menus = new StringBuilder();
		StringBuilder mode_s = new StringBuilder();
		byte breakindex = 0;
		int widgetId;
		RemoteViews views;
		Context context;
		int mode = -1;
		
		DownThread(Context context, int widgetId, RemoteViews views){

			this.context = context;
			this.widgetId = widgetId;
			this.views = views;
		}
		
		public void run() {
			
			boolean file_exists = false;
			boolean data_latest = false;
			boolean no_error = false;
			boolean month_latest = false;
			
			//파일 모드 가져옴
			try{
				FileInputStream in = context.openFileInput("widgetMode.txt");
				//Fileinputstream을 String으로 변환. 참고주소 -> http://nmshome.tistory.com/entry/FileInputStream-%EC%9D%B4%EC%9A%A9%EC%82%AC%EB%A1%80
				StringTokenizer st;				
				int count = in.available();
				byte b[] = new byte[count];
				int total = in.read(b);
				String prt = new String(b);
				byte tkb[] = {0x0d, 0x0a};
				String tk = new String(tkb);
				st = new StringTokenizer(prt,tk);
				int sk = st.countTokens();
				for(int i = 0 ; i < sk ; i++){
					mode_s.append(st.nextToken());
					mode_s.append("\n");
				}					
				in.close();	
				
				if (mode_s.toString().contains("자동모드"))
					mode = 1;
				else if (mode_s.toString().contains("절약모드"))
					mode = 2;
				
			}catch (Exception e) {				
			}
			
			//저장된 데이터가 있는지 조사
			try{
				FileInputStream in = context.openFileInput("menus.txt");
				in.close();
				file_exists = true;
			} catch (Exception e) {
				file_exists = false;
			}
			
			if (mode == -1){//저장된 mode가 없는 경우
				menus.append("FileError");
			}else {//저장된 mode가 있는 경우

				//파일이 존재하는 경우, 최신데이터인지 조사한다.
				if (file_exists){//파일이 존재하는 경우.
					 
					//파일이 최신인지 확인한다.
					try{//menus 를 파일로부터 가져온다.
						FileInputStream in = context.openFileInput("menus.txt");
						//Fileinputstream을 String으로 변환. 참고주소 -> http://nmshome.tistory.com/entry/FileInputStream-%EC%9D%B4%EC%9A%A9%EC%82%AC%EB%A1%80
						StringTokenizer st;				
						int count = in.available();
						byte b[] = new byte[count];
						int total = in.read(b);
						String prt = new String(b);
						byte tkb[] = {0x0d, 0x0a};
						String tk = new String(tkb);
						st = new StringTokenizer(prt,tk);
						int sk = st.countTokens();
						for(int i = 0 ; i < sk ; i++){
							menus.append(st.nextToken());
							menus.append("\n");
						}					
						in.close();					
					}catch (Exception e) {
						menus.append("FinputError1");				
					}
					
					//최신데이터인지 아닌지 테스트.
					no_error = !menus.toString().contains("Error");
					if (no_error) {
						int sample_s = 0;
						int sample_e = 0;
						boolean data_ok = false;
						
						for (int i = 0 ; i < 25 ; i++){
							sample_s = menus.indexOf("m", sample_e);
							sample_e = menus.indexOf("e", sample_s);
							data_ok = !(menus.substring(sample_s, sample_e).contains("등록된 식단이 없습니다"));
							if (data_ok)
								break;
							else
								continue;
						}
						
						month_latest = (menus.substring(0, 2).contains(cal.get(Calendar.MONTH) + ""));
						data_latest = month_latest && data_ok;				
					}
					
				}//데이터 판단 완료.
				
					
				if (data_latest && no_error) {//최신데이터인 경우
					try{						
						//메뉴 수정
						int start = menus.indexOf(cal.get(Calendar.MONTH) + "." + cal.get(Calendar.DATE));
						start = menus.indexOf("m", start + 1);
						int end = menus.indexOf("e", start);
						menus = new StringBuilder(menus.substring(start, end + 1));
						
						if(menus.toString().contains("등록된 식단이 없습니다")){
							menus.append("nomenu");
						}else {
							start = menus.indexOf("\n");
							while(start != -1){
								menus.replace(start, start + 1, "m");
								start = menus.indexOf("\n", start + 1);
							}
						}
						
					} catch (Exception e) {
						menus.append("FeditError1");	
					}
				}else {//최신데이터가 아니거나, 데이터가 없는 경우
					
					if (month_latest == false)
						context.deleteFile("menus.txt");
					
					menus = new StringBuilder();
					
					if (mode == 1){
						
						StringBuilder menubuilder1 = new StringBuilder();
		    			int[] startDate = {cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK)};
		    			int[] endDate = new int[4];
		    			int[] currDate = startDate;
		    			
		    			//이번달 메뉴 Fetch
		    			startDate = moveTheDate(startDate, 1 - startDate[2]);
		    			endDate = moveTheDate(startDate, cal.getActualMaximum(Calendar.DATE) - 1);
		    			menubuilder1 = getTheDateMenu(startDate, endDate);
		    					    			
		    			//다운받은 데이터 저장하기 전에 저장할만한 데이터인지 테스트.
		    			no_error = !menubuilder1.toString().contains("Error");
		    			data_latest = false;
		    			month_latest = false;
						if (no_error) {					
							month_latest = (menubuilder1.substring(0, 2).contains(cal.get(Calendar.MONTH) + ""));
							data_latest = month_latest;			
						}//테스트 완료.
						
						if (data_latest || !file_exists ){
							//파일에 데이터 저장.
			    			try{
			    				FileOutputStream fos = context.openFileOutput("menus.txt", Context.MODE_WORLD_READABLE);
			    				fos.write(menubuilder1.toString().getBytes());
			    				fos.close();
			    			} catch (Exception e) {
			    				menus.append("FoutputError1");
			    			}
						}
					}
					
					
					//데이터Fetch				
					try{
						FileInputStream in = context.openFileInput("menus.txt");
						//Fileinputstream을 String으로 변환. 참고주소 -> http://nmshome.tistory.com/entry/FileInputStream-%EC%9D%B4%EC%9A%A9%EC%82%AC%EB%A1%80
						StringTokenizer st;				
						int count = in.available();
						byte b[] = new byte[count];
						int total = in.read(b);
						String prt = new String(b);
						byte tkb[] = {0x0d, 0x0a};
						String tk = new String(tkb);
						st = new StringTokenizer(prt,tk);
						int sk = st.countTokens();
						for(int i = 0 ; i < sk ; i++){
							menus.append(st.nextToken());
							menus.append("\n");
						}					
						in.close();		
						
												
						//메뉴수정
						int start = menus.indexOf(cal.get(Calendar.MONTH) + "." + cal.get(Calendar.DATE));
						start = menus.indexOf("m", start + 1);
						int end = menus.indexOf("e", start);
						menus = new StringBuilder(menus.substring(start, end + 1));
						
						if(menus.toString().contains("등록된 식단이 없습니다")){
							menus.append("nomenu");
						}else {
							start = menus.indexOf("\n");
							while(start != -1){
								menus.replace(start, start + 1, "m");
								start = menus.indexOf("\n", start + 1);
							}
						}
					}catch (Exception e) {
						menus.append("FileErrorWidget");				
					}
				}//최신 데이터가 없는 경우, Fetch 완료.
			}
			
			
			mAfterDown.sendEmptyMessage(0);//보내기.		
		}
		
		Handler mAfterDown = new Handler(){
			public void handleMessage(Message msg){
				//메뉴초기화//
				views.setTextViewText(R.id.menu1, "");
				views.setTextViewText(R.id.menu2, "");
				views.setTextViewText(R.id.menu3, "");
				views.setTextViewText(R.id.menu4, "");
				views.setTextViewText(R.id.menu5, "");
				views.setTextViewText(R.id.menu6, "");
				views.setTextViewText(R.id.menu7, "");
				//메뉴초기화 끝// 
				
				
				if (menus.toString().contains("FileError")){
					if (mode == -1){
					views.setTextViewText(R.id.menu1, "이 위젯을 제거하시고");
					views.setTextViewText(R.id.menu2, "다시 생성하세요.");
					}else{
						views.setTextViewText(R.id.menu1, "다운로드된 식단이");
						views.setTextViewText(R.id.menu2, "없습니다.");
						if (mode == 2){
							views.setTextViewText(R.id.menu3, "앱을 실행하여");
							views.setTextViewText(R.id.menu4, "메뉴를 다운해주세요!");
						}else if(mode == 1){
							views.setTextViewText(R.id.menu3, "위젯을 클릭하여");
							views.setTextViewText(R.id.menu4, "위젯을 업데이트하거나");
							views.setTextViewText(R.id.menu5, "앱을 실행하여");
							views.setTextViewText(R.id.menu6, "메뉴를 다운해주세요!");
						}
					}
					
					
				}else if(menus.toString().contains("StrError")){
					views.setTextViewText(R.id.menu1, "홈페이지 구조가");
					views.setTextViewText(R.id.menu2, "바뀌었습니다.");
					views.setTextViewText(R.id.menu3, "개발자에게");
					views.setTextViewText(R.id.menu4, "알려주세요!!");
					views.setTextViewText(R.id.menu5, "개발자 이메일은");
					views.setTextViewText(R.id.menu6, "heostar@gmail.com");
					views.setTextViewText(R.id.menu7, "입니다^^");
				}else if (menus.toString().contains("NetError")){
					
					views.setTextViewText(R.id.menu1, "메뉴생성을");
					views.setTextViewText(R.id.menu2, "실패하였습니다ㅠㅠ");
					views.setTextViewText(R.id.menu3, "네트워크상태를");
					views.setTextViewText(R.id.menu4, "확인하시고");
					
					if (mode == 1){
						views.setTextViewText(R.id.menu5, "위젯을 클릭하여");
						views.setTextViewText(R.id.menu6, "다시 업데이트하세요!");
					}else if (mode ==2){
						views.setTextViewText(R.id.menu5, "앱을 실행하여");
						views.setTextViewText(R.id.menu6, "다시 업데이트하세요!");
					}
					
				}else if (menus.toString().contains("nomenu")){
					views.setTextViewText(R.id.menu1, "등록된 식단이 없습니다.");
					views.setTextViewText(R.id.menu2, "평일인데도 없다면");
					if (mode == 1)
						views.setTextViewText(R.id.menu3, "영양사님께 문의를...");
					else if (mode == 2){
						views.setTextViewText(R.id.menu3, "앱을 실행하여");
						views.setTextViewText(R.id.menu4, "메뉴를 업데이트하세요.");
						views.setTextViewText(R.id.menu5, "그래도 식단이 없다면");
						views.setTextViewText(R.id.menu6, "영양사님께 문의를...");
					}
				}else{
					while (menus.indexOf("/") != -1){
						int target = menus.indexOf("/");
						menus.deleteCharAt(target);
					}
					
					String menulist = menus.toString();
					
					int[] menuNumber = {0,0,0,0,0,0,0,0};
					int parameter = -1;
					
					for (int counter = 0 ; counter < menuNumber.length ; counter++){
						parameter = menulist.indexOf("m",parameter+1);
						if (parameter == -1){
							menuNumber[counter] = menulist.indexOf("e");
							break;
						}else {
							menuNumber[counter] = parameter;
						}
					}
					
					if (menuNumber[1] != 0){
						views.setTextViewText(R.id.menu1, menulist.substring(menuNumber[0] + 1, menuNumber[1] ));	
					}
					if (menuNumber[2] != 0){
						views.setTextViewText(R.id.menu2, menulist.substring(menuNumber[1] + 1, menuNumber[2] ));	
					}
					if (menuNumber[3] != 0){
						views.setTextViewText(R.id.menu3, menulist.substring(menuNumber[2] + 1, menuNumber[3] ));	
					}
					if (menuNumber[4] != 0){
						views.setTextViewText(R.id.menu4, menulist.substring(menuNumber[3] + 1, menuNumber[4] ));
					}
					if (menuNumber[5] != 0){
						views.setTextViewText(R.id.menu5, menulist.substring(menuNumber[4] + 1, menuNumber[5] ));	
					}
					if (menuNumber[6] != 0){
						views.setTextViewText(R.id.menu6, menulist.substring(menuNumber[5] + 1, menuNumber[6] ));	
					}
					if (menuNumber[7] != 0){
						views.setTextViewText(R.id.menu7, menulist.substring(menuNumber[6] + 1, menuNumber[7] ));	
					}
				
				}
				
				AppWidgetManager.getInstance(context).updateAppWidget(widgetId, views);
			}
		};
	
	}
	//멀티스레드 끝//
	
	public StringBuilder getTheDateMenu (int startdate[], int enddate[]){
		
		int month = startdate[1];
		int date = startdate[2];
		boolean brcheck = false;
		
		StringBuilder theMenus = new StringBuilder();
		
		try{
				URL url = new URL("http://www.meary.sc.kr/index.jsp?mnu=M001009004002&SCODE=S0000000124&frame=&year="+startdate[0]+"&month="+startdate[1]+"&cmd=cal");
				HttpURLConnection conn = (HttpURLConnection)url.openConnection();
				if (conn != null){
					conn.setConnectTimeout(9000);
					conn.setUseCaches(false);
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK){
						
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "euc-kr"));
						
						while (date <= enddate[2]){
							String line1 = br.readLine();
							if (line1.contains("schedule-list")){
								while(date <= enddate[2]){
									line1 = br.readLine();
									if (line1.contains(">"+date+"<")){
										theMenus.append(month + "." + date + "m");
										date++;
										brcheck = false;
										for(;;){
											line1 = br.readLine();
											if (line1.contains("m_wrap") || line1.contains("달력보기")){
												if (brcheck == false){
													theMenus.append("등록된 식단이 없습니다.");
												}
												theMenus.append("e");
												break;
											}else if(line1.contains("content")){												
												int start = line1.indexOf("content",0);
												theMenus.append(line1.substring(start+9));
												if(line1.contains("</div>")){
													theMenus.append("e");
													break;
												}
												continue;											
											}else if(line1.contains("br")){
												brcheck = line1.contains("br");
												int start = line1.indexOf(">",0);
												theMenus.append("\n"+line1.substring(start+1));
												continue;
											}else if(line1.contains("</html>")){
												theMenus.append("StrError");
												date = enddate[2] + 1;
												break;
											}
										}
									}											
								}
							}
							else if (line1.contains("</html>")){
								theMenus.append("StrError");
								date = enddate[2] + 1;
							}
						}
						br.close();
					}
				}
				conn.disconnect();
		}catch (Exception ex){
			theMenus.append("NetError");
		}
		
		
		while (theMenus.indexOf("/") != -1){
			int target = theMenus.indexOf("/");
			theMenus.deleteCharAt(target);
		}
		
		while (theMenus.indexOf("<div>") != -1){
			int target = theMenus.indexOf("<div>");
			theMenus.deleteCharAt(target);
			theMenus.deleteCharAt(target);
			theMenus.deleteCharAt(target);
			theMenus.deleteCharAt(target);
			theMenus.deleteCharAt(target);
		}
				
		return theMenus;
	}
    
    public int[] moveTheDate (int[] thedate, int movement){
		
		int[] daty = new int[4];
		daty[0] = thedate[0];
		daty[1] = thedate[1];
		daty[2] = thedate[2];
		daty[3] = thedate[3];
		
		int[] orgdaty = new int[4];
		orgdaty[0] = cal.get(Calendar.YEAR);
		orgdaty[1] = cal.get(Calendar.MONTH);
		orgdaty[2] = cal.get(Calendar.DATE);
		orgdaty[3] = cal.get(Calendar.DAY_OF_WEEK);
		
		if (movement > 0){
			
			if (daty[1] == 11){
				if (daty[2] + movement > 31){
					daty[0]++;
					daty[1] = 0;
					daty[2] = daty[2] + movement - 31;					
				}else{
					daty[2] = daty[2] + movement;
				}
			}else {
				cal.set(Calendar.MONTH, daty[1]);
				if (daty[2] + movement > cal.getActualMaximum(Calendar.DATE)){
					daty[1]++;
					daty[2] = daty[2] + movement - cal.getActualMaximum(Calendar.DATE);					
				}else{
					daty[2] = daty[2] + movement;
				}
				cal.set(Calendar.MONTH, orgdaty[1]);
			}
			
		}else if (movement < 0){
			
			if (daty[1] == 0){
				if (daty[2] + movement <= 0){
					daty[0]--;
					daty[1] = 11;
					daty[2] = 31 + daty[2] + movement;					
				}else{
					daty[2] = daty[2] + movement;
				}
			}else {
				if (daty[2] + movement <= 0){
					cal.set(orgdaty[0], orgdaty[1] - 1, 1);
					daty[1]--;
					daty[2] = cal.getActualMaximum(Calendar.DATE) + daty[2] + movement;
					cal.set(orgdaty[0], orgdaty[1], orgdaty[2]);
				}else{
					daty[2] = daty[2] + movement;
				}
			}
		}
		
		cal.set(daty[0], daty[1], daty[2]);
		daty[3] = cal.get(Calendar.DAY_OF_WEEK);
		cal.set(orgdaty[0], orgdaty[1], orgdaty[2]);
		
		return daty;
	}
}
