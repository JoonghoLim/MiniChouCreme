package com.wisdompark.minichoucreme.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wisdompark.minichoucreme.R;
import com.wisdompark.minichoucreme.engin.MiniChouContext;
import com.wisdompark.minichoucreme.storage.FPrintInfo;
import com.wisdompark.minichoucreme.storage.PlaceInfo;
import com.wisdompark.minichoucreme.utils.Constraints;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    List<FPrintInfo> messages = new ArrayList<FPrintInfo>();
    Context context;

    public MessageAdapter(Context context, ArrayList<FPrintInfo> list) {
        this.context = context;
        messages = list;
    }

    /*
    public void add(Message message) {
        this.messages.add(message);
        notifyDataSetChanged(); // to render the list we need to notify
    }
*/
    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    // This is the backbone of the class, it handles the creation of single ListView row (chat bubble)
    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        MessageViewHolder holder = new MessageViewHolder();
        LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        FPrintInfo message = messages.get(i);
        long msgTime = message.getmTime();
        PlaceInfo pInfo = message.getmPlaceInfo();
        String key = pInfo.getKey();
        String macAddr = pInfo.getMacList().get(0);
        String apName = pInfo.getApList().get(0);
        String msg = "";
        String strTime = "";

        if(pInfo.getMacList().get(0).equals(Constraints.OUTPLACE_MAC)){
            //msg = "등록되지 않은 지역에 있습니다";
            msg = Constraints.STR_OUT_PLACE;
        }else{
            msg = "' "+pInfo.getKey()+" ' 주변에 있습니다";
        }

        SimpleDateFormat sf = new SimpleDateFormat("MM월dd일 HH시mm분");
        strTime = sf.format(msgTime);


        //if (message.isBelongsToCurrentUser()) { // this message was sent by us so let's create a basic chat bubble on the right
        if (message.getmSenderID().equals(MiniChouContext.getmUserID())) { // this message was sent by us so let's create a basic chat bubble on the right
            convertView = messageInflater.inflate(R.layout.msg_type_my_message, null);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);
            holder.messageBody.setText(msg+"\n@"+strTime);
        } else { // this message was sent by someone else so let's create an advanced chat bubble on the left
            convertView = messageInflater.inflate(R.layout.msg_type_their_message, null);
            holder.avatar = (View) convertView.findViewById(R.id.avatar);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.messageBody = (TextView) convertView.findViewById(R.id.message_body);
            convertView.setTag(holder);

            holder.name.setText(message.getmSenderID());
            holder.messageBody.setText(msg+"\n@"+strTime);
            GradientDrawable drawable = (GradientDrawable) holder.avatar.getBackground();
            drawable.setColor(Color.rgb(158,217,234));
        }

        return convertView;
    }

}

class MessageViewHolder {
    public View avatar;
    public TextView name;
    public TextView messageBody;
}