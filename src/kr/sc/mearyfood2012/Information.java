package kr.sc.mearyfood2012;
import java.util.*;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;

public class Information extends Activity implements AdapterView.OnItemClickListener{
	
	ArrayList<String> infoList;	
	ArrayAdapter<String> infoAdapter;
	ListView infolist;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.information);
		
		infoList = new ArrayList<String>();
		infoList.add("�Ĵܾ�����Ʈ �ȳ�");
		infoList.add("���� ����");		
		infoList.add("������ �ı�");
		
		infoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, infoList);
		
		infolist = (ListView)findViewById(R.id.listView_info);
		infolist.setAdapter(infoAdapter);	
		infolist.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView parent, View view, int position, long id){
		
		if(position == 0){
			new AlertDialog.Builder(this)
			.setTitle("�Ĵܾ�����Ʈ �ȳ�")
			.setMessage(R.string.guide2)
			.setNegativeButton("�ݱ�", null)
			.show();
		}else if(position == 1){
			new AlertDialog.Builder(this)
			.setTitle("���� ����")
			.setMessage(R.string.guide1)
			.setNegativeButton("�ݱ�", null)
			.show();
		}else if(position == 2){
			new AlertDialog.Builder(this)
			.setTitle("������ �ı�")
			.setMessage(R.string.hugi)
			.setNegativeButton("�ݱ�", null)
			.show();
		}
		
	}
}
