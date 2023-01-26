package kr.co.smartbank.app.view.push

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.push_hist_list_item.view.*
import kr.co.smartbank.app.R
import java.text.SimpleDateFormat
import java.util.*

class PushHistListAdapter(private val items: MutableList<PushHistListItem>): BaseAdapter(){
    override fun getCount(): Int = items.size
    override fun getItem(position: Int): PushHistListItem = items[position]
    override fun getItemId(position: Int): Long = position.toLong()

    @SuppressLint("SetTextI18n")
    override fun getView(position: Int, view: View?, parent: ViewGroup?): View {
        var convertView = view
        if (convertView == null) convertView = LayoutInflater.from(parent?.context).inflate(R.layout.push_hist_list_item, parent, false)

        val item: PushHistListItem = items[position]
        //convertView!!.image_title.setImageDrawable(item.icon)
        convertView!!.push_hist_list_item_title.text=item.title
        convertView.push_hist_list_item_body.text=item.body


        val t_dateFormat0 = SimpleDateFormat("yyyyMMddkkmmss", Locale("ko", "KR"))
        val t_date = t_dateFormat0.parse(item.date);
        // 날짜, 시간을 가져오고 싶은 형태 선언
        val t_dateFormat = SimpleDateFormat("yyyy-MM-dd (E)", Locale("ko", "KR"))
        val t_dateFormat2 = SimpleDateFormat("HH:mm:ss", Locale("ko", "KR"))

        convertView.push_hist_list_item_date.text="${t_date?.let { t_dateFormat.format(it) }}\n${t_date?.let {
            t_dateFormat2.format(
                it
            )
        }}"

        val imageV:ImageView = convertView.push_hist_list_item_image
        Glide.with(convertView.context)
                .load(item.image)
                .into(imageV)
        return convertView
    }
}
