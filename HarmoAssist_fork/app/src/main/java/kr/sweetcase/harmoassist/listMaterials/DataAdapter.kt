package kr.sweetcase.harmoassist.listMaterials

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.sweetcase.harmoassist.R
import kotlinx.android.synthetic.main.data_list_item.view.*

class DataAdapter(
    val items :ArrayList<String>,
    val context :Context
) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.data_list_item,
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvDataType.text = items[position]
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // TextView에 각 데이터 항목을 가져오기위해 리소스로부터
        val tvDataType = view.tv_data_type!!
    }


}
