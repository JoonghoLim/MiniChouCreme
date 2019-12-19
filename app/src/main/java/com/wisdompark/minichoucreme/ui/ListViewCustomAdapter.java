package com.wisdompark.minichoucreme.ui;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.storage.FPrintInfo;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.utils.Constraints;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ListViewCustomAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<PlaceInfo> listViewItemList = new ArrayList<PlaceInfo>() ;

    // ListViewAdapter의 생성자
    public ListViewCustomAdapter(ArrayList<PlaceInfo> dataList) {
        listViewItemList = dataList;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_view_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1) ;
        TextView timeTextView = (TextView) convertView.findViewById(R.id.textView2) ;

        iconImageView.setVisibility(View.GONE);
        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        PlaceInfo pInfo = listViewItemList.get(position);
        String msg = "";

        // 아이템 내 각 위젯에 데이터 반영
        //iconImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_menu_slideshow));
        //titleTextView.setText(notiItem.getPlace());
        titleTextView.setText(pInfo.getKey());
        int count = pInfo.getApList().size();
        msg = pInfo.getApList().get(0);
        if(count > 1)
            msg = msg + " 외 "+(count-1);

        timeTextView.setText(msg);

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }
}