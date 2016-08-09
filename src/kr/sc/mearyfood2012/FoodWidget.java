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
		
		//appWidgetIds ����
		try{
			FileOutputStream fos = context.openFileOutput("appWidgetIds.txt", Context.MODE_WORLD_READABLE);
			fos.write(idbuilder.toString().getBytes());
			fos.close();
		}catch (Exception e){			
		}
		
	}
	
	public void UpdateMenus(Context context, AppWidgetManager appWidgetManager, int widgetId){
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widgetw);
				
		//�ȳ�����ǥ��//
		views.setTextViewText(R.id.menu1, "������Ʈ��...");
		views.setTextViewText(R.id.menu2, "�ִ�ҿ�ð�10��");
		views.setTextViewText(R.id.menu3, "10�ʸ� �ѱ� ��");
		views.setTextViewText(R.id.menu4, "�ٽ� ������Ʈ�ϼ���.");
		views.setTextViewText(R.id.menu5, " ");
		views.setTextViewText(R.id.menu6, " ");
		views.setTextViewText(R.id.menu7, " ");
		//�ȳ�����ǥ�ÿϷ�//
		
			
		
		//���ȭ�� ���� ����
		SharedPreferences prefs = context.getSharedPreferences(PREF, 0);
		Long target = prefs.getLong("bG_" + widgetId, 0);
		changeBG(views,target);
		//���ȭ�� ���� �Ϸ�
		
		//�ð��ؽ�Ʈ ����
		StringBuilder time = new StringBuilder();
		Calendar cal = new GregorianCalendar();
		
		StringBuilder dayOfWeek = new StringBuilder();
		if (cal.get(Calendar.DAY_OF_WEEK) == 1){
			dayOfWeek.append("�Ͽ���");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 2){
			dayOfWeek.append("������");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 3){
			dayOfWeek.append("ȭ����");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 4){
			dayOfWeek.append("������");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 5){
			dayOfWeek.append("�����");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 6){
			dayOfWeek.append("�ݿ���");
		}else if(cal.get(Calendar.DAY_OF_WEEK) == 7){
			dayOfWeek.append("�����");
		}
		
		time.append(String.format("%d�� %d�� %d�� " + dayOfWeek.toString(), cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH)));
		views.setTextViewText(R.id.date, time.toString());
		//�ð��ؽ�Ʈ ���� �Ϸ�
		
		//����������Ʈ ����Ʈ//
		Intent intentpop = new Intent(context, WidgetUpdateConfirm.class);
		intentpop.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		intentpop.setAction("kr.sc.mearyfood2012.ACTION_MANUAL_UPDATE");
		PendingIntent pending = PendingIntent.getActivity(context, widgetId, intentpop, PendingIntent.FLAG_UPDATE_CURRENT);
		views.setOnClickPendingIntent(R.id.bGimage, pending);
		//Ŭ���� WidgetUpdateConfirm Activity�� �̵�
				
		appWidgetManager.updateAppWidget(widgetId, views);
		
		//�޴� ���� ��Ƽ������//
		mThread = new DownThread(context, widgetId, views);
		mThread.start();
		//�ٿ����� �̵�//
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

	//�̰��� ��Ƽ������//
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
			
			//���� ��� ������
			try{
				FileInputStream in = context.openFileInput("widgetMode.txt");
				//Fileinputstream�� String���� ��ȯ. �����ּ� -> http://nmshome.tistory.com/entry/FileInputStream-%EC%9D%B4%EC%9A%A9%EC%82%AC%EB%A1%80
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
				
				if (mode_s.toString().contains("�ڵ����"))
					mode = 1;
				else if (mode_s.toString().contains("������"))
					mode = 2;
				
			}catch (Exception e) {				
			}
			
			//����� �����Ͱ� �ִ��� ����
			try{
				FileInputStream in = context.openFileInput("menus.txt");
				in.close();
				file_exists = true;
			} catch (Exception e) {
				file_exists = false;
			}
			
			if (mode == -1){//����� mode�� ���� ���
				menus.append("FileError");
			}else {//����� mode�� �ִ� ���

				//������ �����ϴ� ���, �ֽŵ��������� �����Ѵ�.
				if (file_exists){//������ �����ϴ� ���.
					 
					//������ �ֽ����� Ȯ���Ѵ�.
					try{//menus �� ���Ϸκ��� �����´�.
						FileInputStream in = context.openFileInput("menus.txt");
						//Fileinputstream�� String���� ��ȯ. �����ּ� -> http://nmshome.tistory.com/entry/FileInputStream-%EC%9D%B4%EC%9A%A9%EC%82%AC%EB%A1%80
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
					
					//�ֽŵ��������� �ƴ��� �׽�Ʈ.
					no_error = !menus.toString().contains("Error");
					if (no_error) {
						int sample_s = 0;
						int sample_e = 0;
						boolean data_ok = false;
						
						for (int i = 0 ; i < 25 ; i++){
							sample_s = menus.indexOf("m", sample_e);
							sample_e = menus.indexOf("e", sample_s);
							data_ok = !(menus.substring(sample_s, sample_e).contains("��ϵ� �Ĵ��� �����ϴ�"));
							if (data_ok)
								break;
							else
								continue;
						}
						
						month_latest = (menus.substring(0, 2).contains(cal.get(Calendar.MONTH) + ""));
						data_latest = month_latest && data_ok;				
					}
					
				}//������ �Ǵ� �Ϸ�.
				
					
				if (data_latest && no_error) {//�ֽŵ������� ���
					try{						
						//�޴� ����
						int start = menus.indexOf(cal.get(Calendar.MONTH) + "." + cal.get(Calendar.DATE));
						start = menus.indexOf("m", start + 1);
						int end = menus.indexOf("e", start);
						menus = new StringBuilder(menus.substring(start, end + 1));
						
						if(menus.toString().contains("��ϵ� �Ĵ��� �����ϴ�")){
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
				}else {//�ֽŵ����Ͱ� �ƴϰų�, �����Ͱ� ���� ���
					
					if (month_latest == false)
						context.deleteFile("menus.txt");
					
					menus = new StringBuilder();
					
					if (mode == 1){
						
						StringBuilder menubuilder1 = new StringBuilder();
		    			int[] startDate = {cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK)};
		    			int[] endDate = new int[4];
		    			int[] currDate = startDate;
		    			
		    			//�̹��� �޴� Fetch
		    			startDate = moveTheDate(startDate, 1 - startDate[2]);
		    			endDate = moveTheDate(startDate, cal.getActualMaximum(Calendar.DATE) - 1);
		    			menubuilder1 = getTheDateMenu(startDate, endDate);
		    					    			
		    			//�ٿ���� ������ �����ϱ� ���� �����Ҹ��� ���������� �׽�Ʈ.
		    			no_error = !menubuilder1.toString().contains("Error");
		    			data_latest = false;
		    			month_latest = false;
						if (no_error) {					
							month_latest = (menubuilder1.substring(0, 2).contains(cal.get(Calendar.MONTH) + ""));
							data_latest = month_latest;			
						}//�׽�Ʈ �Ϸ�.
						
						if (data_latest || !file_exists ){
							//���Ͽ� ������ ����.
			    			try{
			    				FileOutputStream fos = context.openFileOutput("menus.txt", Context.MODE_WORLD_READABLE);
			    				fos.write(menubuilder1.toString().getBytes());
			    				fos.close();
			    			} catch (Exception e) {
			    				menus.append("FoutputError1");
			    			}
						}
					}
					
					
					//������Fetch				
					try{
						FileInputStream in = context.openFileInput("menus.txt");
						//Fileinputstream�� String���� ��ȯ. �����ּ� -> http://nmshome.tistory.com/entry/FileInputStream-%EC%9D%B4%EC%9A%A9%EC%82%AC%EB%A1%80
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
						
												
						//�޴�����
						int start = menus.indexOf(cal.get(Calendar.MONTH) + "." + cal.get(Calendar.DATE));
						start = menus.indexOf("m", start + 1);
						int end = menus.indexOf("e", start);
						menus = new StringBuilder(menus.substring(start, end + 1));
						
						if(menus.toString().contains("��ϵ� �Ĵ��� �����ϴ�")){
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
				}//�ֽ� �����Ͱ� ���� ���, Fetch �Ϸ�.
			}
			
			
			mAfterDown.sendEmptyMessage(0);//������.		
		}
		
		Handler mAfterDown = new Handler(){
			public void handleMessage(Message msg){
				//�޴��ʱ�ȭ//
				views.setTextViewText(R.id.menu1, "");
				views.setTextViewText(R.id.menu2, "");
				views.setTextViewText(R.id.menu3, "");
				views.setTextViewText(R.id.menu4, "");
				views.setTextViewText(R.id.menu5, "");
				views.setTextViewText(R.id.menu6, "");
				views.setTextViewText(R.id.menu7, "");
				//�޴��ʱ�ȭ ��// 
				
				
				if (menus.toString().contains("FileError")){
					if (mode == -1){
					views.setTextViewText(R.id.menu1, "�� ������ �����Ͻð�");
					views.setTextViewText(R.id.menu2, "�ٽ� �����ϼ���.");
					}else{
						views.setTextViewText(R.id.menu1, "�ٿ�ε�� �Ĵ���");
						views.setTextViewText(R.id.menu2, "�����ϴ�.");
						if (mode == 2){
							views.setTextViewText(R.id.menu3, "���� �����Ͽ�");
							views.setTextViewText(R.id.menu4, "�޴��� �ٿ����ּ���!");
						}else if(mode == 1){
							views.setTextViewText(R.id.menu3, "������ Ŭ���Ͽ�");
							views.setTextViewText(R.id.menu4, "������ ������Ʈ�ϰų�");
							views.setTextViewText(R.id.menu5, "���� �����Ͽ�");
							views.setTextViewText(R.id.menu6, "�޴��� �ٿ����ּ���!");
						}
					}
					
					
				}else if(menus.toString().contains("StrError")){
					views.setTextViewText(R.id.menu1, "Ȩ������ ������");
					views.setTextViewText(R.id.menu2, "�ٲ�����ϴ�.");
					views.setTextViewText(R.id.menu3, "�����ڿ���");
					views.setTextViewText(R.id.menu4, "�˷��ּ���!!");
					views.setTextViewText(R.id.menu5, "������ �̸�����");
					views.setTextViewText(R.id.menu6, "heostar@gmail.com");
					views.setTextViewText(R.id.menu7, "�Դϴ�^^");
				}else if (menus.toString().contains("NetError")){
					
					views.setTextViewText(R.id.menu1, "�޴�������");
					views.setTextViewText(R.id.menu2, "�����Ͽ����ϴ٤Ф�");
					views.setTextViewText(R.id.menu3, "��Ʈ��ũ���¸�");
					views.setTextViewText(R.id.menu4, "Ȯ���Ͻð�");
					
					if (mode == 1){
						views.setTextViewText(R.id.menu5, "������ Ŭ���Ͽ�");
						views.setTextViewText(R.id.menu6, "�ٽ� ������Ʈ�ϼ���!");
					}else if (mode ==2){
						views.setTextViewText(R.id.menu5, "���� �����Ͽ�");
						views.setTextViewText(R.id.menu6, "�ٽ� ������Ʈ�ϼ���!");
					}
					
				}else if (menus.toString().contains("nomenu")){
					views.setTextViewText(R.id.menu1, "��ϵ� �Ĵ��� �����ϴ�.");
					views.setTextViewText(R.id.menu2, "�����ε��� ���ٸ�");
					if (mode == 1)
						views.setTextViewText(R.id.menu3, "�����Բ� ���Ǹ�...");
					else if (mode == 2){
						views.setTextViewText(R.id.menu3, "���� �����Ͽ�");
						views.setTextViewText(R.id.menu4, "�޴��� ������Ʈ�ϼ���.");
						views.setTextViewText(R.id.menu5, "�׷��� �Ĵ��� ���ٸ�");
						views.setTextViewText(R.id.menu6, "�����Բ� ���Ǹ�...");
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
	//��Ƽ������ ��//
	
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
											if (line1.contains("m_wrap") || line1.contains("�޷º���")){
												if (brcheck == false){
													theMenus.append("��ϵ� �Ĵ��� �����ϴ�.");
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
