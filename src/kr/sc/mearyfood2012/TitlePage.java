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

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

public class TitlePage extends Activity {

	Calendar cal = new GregorianCalendar();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.titlepage);
        
        final TextView loadStatus = (TextView)findViewById(R.id.load_status);
        
        new CountDownTimer(1100, 1){        	
        	public void onTick(long millisUntilFinished){
        	}
        	public void onFinish(){
        		 loadStatus.setText("��Ʈ��ũ ���� Ȯ����");
        		 ConnectivityManager mgr = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        	     boolean i_ok = (mgr.getAllNetworkInfo()[0]).isConnected() ||  (mgr.getAllNetworkInfo()[1]).isConnected();
        	        
        	     if (i_ok == false){
        	    	loadStatus.setText("�Ĵ� ������Ʈ ����\n(��밡���� ��Ʈ��ũ ����)");
         	     	new CountDownTimer(1000, 1){
         	        	public void onTick(long millisUntilFinished){
         	        	}
         	        	public void onFinish(){
         	        		try{
         	        			StringBuilder menus = new StringBuilder();
         	        			
         	        			FileInputStream in = openFileInput("menus.txt");
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
         						
         						boolean month_latest = (menus.substring(0, 2).contains(cal.get(Calendar.MONTH) + ""));
         						
         						if(!month_latest){
         							deleteFile("menus.txt");
         						}         						
         	        			
         	        		}catch (Exception e){           	        			
         	        		}
         	        		
         	        		Intent intenty = new Intent();
         	        		intenty.setClass(getApplicationContext(), MainActivity.class);
         	        		startActivity(intenty);        	        		
         	        		finish();        		
         	        	}        	
         	        }.start();         	     	
        	     }else{
        	    	 loadStatus.setText("����� �Ĵ� Ȯ����...");
        	    	 new CountDownTimer(500, 1){
     	    			public void onTick(long millisUntilFinished){
          	        	}
          	        	public void onFinish(){
          	        		
          	        		//������ üũ ����
          	        		boolean data_ok;
          	        		boolean data_pre;
          	        		boolean data_post;
          	        		
                	    	try{
         	        			StringBuilder menus = new StringBuilder();
         	        			
         	        			FileInputStream in = openFileInput("menus.txt");
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
         						
         						data_ok = (menus.substring(0, 2).contains(cal.get(Calendar.MONTH) + ""));
         						
         						if(data_ok){
         							int sample_s = 0;
         							int sample_e = 0;
         							for(int i = 0 ; i < 25 ; i++){
         								sample_s = menus.indexOf("m", sample_e);
         								sample_e = menus.indexOf("e", sample_s);
         								data_ok = !(menus.substring(sample_s, sample_e).contains("��ϵ� �Ĵ��� �����ϴ�"));
         								if (data_ok)
         									break;
         								else
         									continue; 								
         							}
         						}
         						
         						//������ �ڷᰡ �ʿ�����, �ʿ��ϸ� �޾ƾ��ϴ��� ����
         						if (cal.get(Calendar.DATE) <= 6){
         							int monthy = cal.get(Calendar.MONTH);
         							if (monthy == 0)
         								monthy = 11;
         							else
         								monthy--;
         							
         							if (menus.toString().contains(monthy + ".10")){
         								int index = menus.indexOf(monthy+".10");
         								int index1 = menus.indexOf("m", index);
         								int index2 = menus.indexOf("e", index1);
         								int index3 = menus.indexOf("m", index2);
         								int index4 = menus.indexOf("e", index3);
         								int index5 = menus.indexOf("m", index4);
         								int index6 = menus.indexOf("e", index5);
         								boolean one = !menus.substring(index1, index2).contains("��ϵ� �Ĵ��� �����ϴ�");
         								boolean two = !menus.substring(index3, index4).contains("��ϵ� �Ĵ��� �����ϴ�");
         								boolean thr = !menus.substring(index5, index6).contains("��ϵ� �Ĵ��� �����ϴ�");
         								data_pre = one || two || thr;
         							}else
         								data_pre = false;
         						}else
         							data_pre = true;
         						
         						//�����޵� ���������� ����
         						if (cal.get(Calendar.DATE) >= cal.getActualMaximum(Calendar.DATE) - 5){
         							int monthy = cal.get(Calendar.MONTH);
         							if (monthy == 11)
         								monthy = 0;
         							else
         								monthy++;
         							
         							if (menus.toString().contains(monthy + ".10")){
         								int index = menus.indexOf(monthy+".10");
         								int index1 = menus.indexOf("m", index);
         								int index2 = menus.indexOf("e", index1);
         								int index3 = menus.indexOf("m", index2);
         								int index4 = menus.indexOf("e", index3);
         								int index5 = menus.indexOf("m", index4);
         								int index6 = menus.indexOf("e", index5);
         								boolean one = !menus.substring(index1, index2).contains("��ϵ� �Ĵ��� �����ϴ�");
         								boolean two = !menus.substring(index3, index4).contains("��ϵ� �Ĵ��� �����ϴ�");
         								boolean thr = !menus.substring(index5, index6).contains("��ϵ� �Ĵ��� �����ϴ�");
         								data_post = one || two || thr;
         							}else
         								data_post = false;
         						}else
         							data_post = true;
         						
         	        		}catch (Exception e){
         	        			data_ok = false;
         	        			data_pre = false;
         	        			data_post = false;
         	        		}//������ üũ�Ϸ�
                	    	
                	    	if(data_ok && data_pre && data_post){//����� �Ĵ��� �ִٸ�? 
                	    		loadStatus.setText("����� �Ĵ��� �ֽ��ϴ�!");
                	    		new CountDownTimer(800, 1){
                	    			public void onTick(long millisUntilFinished){
                     	        	}
                     	        	public void onFinish(){
                     	        		Intent intenty = new Intent();
                     	        		intenty.setClass(getApplicationContext(), MainActivity.class);
                     	        		startActivity(intenty);        	        		
                     	        		finish();             	        	
                     	        	}
                	    		}.start();
                	    		
                	    	}else{//����� �Ĵ��� ���ٸ�?
                	    		loadStatus.setText("����� �Ĵ��� �����ϴ�.");
                	    		new CountDownTimer(700, 1){
                	    			public void onTick(long millisUntilFinished){
                     	        	}
                     	        	public void onFinish(){
                     	        		loadStatus.setText("�Ĵ� �ٿ�ε� ��...");        	    	
                            	    	new CountDownTimer(10, 1){
                             	        	public void onTick(long millisUntilFinished){
                             	        	}
                             	        	public void onFinish(){ 
                             	        		//�غ�
                                    	    	StringBuilder menubuilder1 = new StringBuilder();
                                    	    	StringBuilder menubuilder2 = new StringBuilder();
                                    	    	StringBuilder menubuilder3 = new StringBuilder();
                                    			int[] startDate = {cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.DAY_OF_WEEK)};
                                    			int[] endDate = new int[4];
                                    			int[] currDate = startDate;
                                    			
                                    			//�̹��� �޴� Fetch
                                    			startDate = moveTheDate(startDate, 1 - startDate[2]);
                                    			endDate = moveTheDate(startDate, cal.getActualMaximum(Calendar.DATE) - 1);
                                    			menubuilder1 = getTheDateMenu(startDate, endDate);
                                    			
                                    			//������ �޴� Fetch
                                    			if(currDate[2] >= cal.getActualMaximum(Calendar.DATE) - 5){
                                        			startDate = moveTheDate(startDate, cal.getActualMaximum(Calendar.DATE) - startDate[2] + 1);
                                        			cal.set(startDate[0], startDate[1], startDate[2]);
                                        			endDate = moveTheDate(startDate, cal.getActualMaximum(Calendar.DATE) - 1);
                                        			cal.set(currDate[0], currDate[1], currDate[2]);
                                        			menubuilder2 = getTheDateMenu(startDate, endDate);
                                    			}
                                    			
                                    			
                                    			//������ �޴� Fetch
                                    			if(currDate[2] <= 6){
                                    				endDate = moveTheDate(currDate, 1 - currDate[2]);
                                        			endDate = moveTheDate(endDate, -1);
                                        			cal.set(endDate[0], endDate[1], endDate[2]);
                                        			startDate = moveTheDate(endDate, 1 - cal.getActualMaximum(Calendar.DATE));
                                        			cal.set(currDate[0], currDate[1], currDate[3]);
                                        			menubuilder3 = getTheDateMenu(startDate, endDate);
                                    			}                    			
                                    			
                                    			
                                    			//���Ͻ�Ʈ�� ����
                                    			try{
                                    				FileOutputStream fos = openFileOutput("menus.txt", Context.MODE_WORLD_READABLE);
                                    				fos.write(menubuilder1.toString().getBytes());
                                    				fos.write(menubuilder2.toString().getBytes());
                                    				fos.write(menubuilder3.toString().getBytes());
                                    				fos.close();
                                    				loadStatus.setText("�Ĵ� �ٿ�ε� �Ϸ�!");
                                    			} catch (Exception e) {
                                    				loadStatus.setText("�Ĵ� ���� ����!");
                                    			}
                                    			
                                    			                			                			        			
                                    	    	new CountDownTimer(800, 1){
                                     	        	public void onTick(long millisUntilFinished){
                                     	        	}
                                     	        	public void onFinish(){
                                     	        		
                                     	        		try{
                                     	        			StringBuilder idbuilder = new StringBuilder();
                                         	        		int[] appWidgetIds = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
                                         	        		FileInputStream in = openFileInput("appWidgetIds.txt");
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
                                         						idbuilder.append(st.nextToken());
                                         						idbuilder.append("\n");
                                         					}                 						
                                         					in.close();
                                         					
                                         					int n = 0;
                                         					for(int walker = idbuilder.indexOf("i") ; walker != -1 ; walker = idbuilder.indexOf("i", walker + 1)){
                                         						appWidgetIds[n] = Integer.parseInt(idbuilder.substring(walker + 1, idbuilder.indexOf("e",walker)));
                                         						n++;
                                         					}
                                         	        		
                                         					//����������Ʈ ��� ������
                                         	        		Intent intentup = new Intent(getApplicationContext(), FoodWidget.class);
                                         	        		intentup.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                                         	        		intentup.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
                                                			sendBroadcast(intentup);
                                     	        		}catch (Exception e){
                                     	        			
                                     	        		}                 	        		
                                            			
                                     	        		//�޾Ƹ��б��Ĵ� ����
                                     	        		Intent intenty = new Intent();
                                     	        		intenty.setClass(getApplicationContext(), MainActivity.class);
                                     	        		startActivity(intenty);        	        		
                                     	        		finish();        		
                                     	        	}        	
                                     	        }.start();   
                                     	        
                             	        	}        	
                             	        }.start();      	        	
                     	        	}
                	    		}.start();        	    		
                	    	}
          	        	}
     	    		 }.start();        	    	    
        	     }
        	}        	
        }.start();       
        
    }
    
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
							Log.d("asdf",""+line1);
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
