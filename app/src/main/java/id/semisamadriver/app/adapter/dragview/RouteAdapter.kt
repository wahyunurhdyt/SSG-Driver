package id.semisamadriver.app.adapter.dragview

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.Route
import kotlinx.android.synthetic.main.item_manage_route.view.*
import java.util.*

class RouteAdapter(var context: Context, var list: MutableList<Route>, var listener: OnStartDragListener): RecyclerView.Adapter<RouteAdapter.MyViewHolder>(), ItemTouchHelperAdapter {

    private var bridge: Bridge? = null
    fun setBridge(bridge: Bridge) {
        this.bridge = bridge
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var name: TextView = itemView.tvName as TextView
        var ivDelete: ImageView = itemView.ivDelete as ImageView
        var container: ConstraintLayout = itemView.container as ConstraintLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_manage_route, parent, false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.name.text = list[position].name
        holder.ivDelete.setOnClickListener {
            bridge?.onClickDelete(list[position], list)
        }
        holder.container.setOnLongClickListener {
            listener.onStartDrag(holder)
            false
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(list, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        bridge?.onItemMove(list)
        return true
    }

    override fun onItemDismis(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }
    interface Bridge{
        fun onClickDelete(item: Route, data: MutableList<Route>)
        fun onItemMove(data: MutableList<Route>)
    }
}