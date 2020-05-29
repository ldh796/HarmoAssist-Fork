package kr.sweetcase.harmoassist.listMaterials

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerDecoration(val divHeight: Int) : RecyclerView.ItemDecoration(){


    fun getItemOffset(outRect: Rect, view: View,
                      parent:RecyclerView,state: RecyclerView.State){
        super.getItemOffsets(outRect,view,parent,state)
        if(parent.getChildAdapterPosition(view) != parent.adapter!!.itemCount-1)
        outRect.bottom = divHeight
    }


}