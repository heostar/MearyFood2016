package kr.sc.mearyfood2012;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;

public class WidgetUpdateConfirm extends Activity {
	
	
	public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);      
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	    
	    new AlertDialog.Builder(this)
	    .setTitle("선택하세요")
	    .setItems(R.array.Uppdate, new DialogInterface.OnClickListener() {
			
			
			public void onClick(DialogInterface dialog, int which) {
				
				if (which == 0){
					Intent intentup = getIntent();
					intentup.setClass(getApplicationContext(), FoodWidget.class);
					sendBroadcast(intentup);
					finish();
				} else if (which == 1){
					Intent intenty = new Intent();
					intenty.setClass(getApplicationContext(), TommorowMenu.class);
					startActivity(intenty);
					finish();
				} else if (which == 2){
					Intent intenty = new Intent();
					intenty.setClass(getApplicationContext(), TitlePage.class);
					startActivity(intenty);
					finish();
				}
			}
		})
	    .setOnCancelListener(new DialogInterface.OnCancelListener() {			
			public void onCancel(DialogInterface dialog) {
				finish();				
			}
		})
		.show();
	}
}
