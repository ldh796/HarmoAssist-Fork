package kr.sweetcase.harmoassist.listMaterials

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_technic.view.*
import kr.sweetcase.harmoassist.R
import kr.sweetcase.harmoassist.TechnicSummaryActivity
import kr.sweetcase.harmoassist.databinding.ItemTechnicBinding
import kr.sweetcase.harmoassist.modules.technicDictionary.TechnicalInfo

/**  기능 설명 리스트를 만들 때 쓰는 리사이클 어댑터 **/
class TechnicAdapter(
    private val items : ArrayList<TechnicalInfo>,
    private val context : Context
) : RecyclerView.Adapter<TechnicAdapter.TechnicViewHolder>() {

    class TechnicViewHolder(val binding : ItemTechnicBinding) : RecyclerView.ViewHolder(binding.root)


    override fun getItemCount(): Int {
        return items.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TechnicViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_technic, parent, false)
        return TechnicViewHolder(ItemTechnicBinding.bind(view))
    }

    override fun onBindViewHolder(holder: TechnicViewHolder, position: Int) {
        holder.binding.technicName = items[position]
        if(items[position].isFavorite)
            holder.binding.technicFavoriteBtn.setImageDrawable(context.resources.getDrawable(R.drawable.favorite_icon))

        // 클릭 리스너
        holder.itemView.setOnClickListener {
            val intent = Intent(context, TechnicSummaryActivity::class.java)
            intent.putExtra("tech_info", items[position])
            context.startActivity(intent)
        }

        //좋아요 리스터
        holder.itemView.technic_favorite_btn.setOnClickListener {

            // 좋아요 해제
            if(items[position].isFavorite) {
                it.technic_favorite_btn.setImageDrawable(context.resources.getDrawable(R.drawable.unfavorite_icon, null))
                items[position].isFavorite = false
                // DB에도 적용
            } else {
                it.technic_favorite_btn.setImageDrawable(context.resources.getDrawable(R.drawable.favorite_icon, null))
                items[position].isFavorite = true
            }
        }
    }
}