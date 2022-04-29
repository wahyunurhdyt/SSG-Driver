package id.semisamadriver.app.adapter.dragview

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemDismis(position: Int)
}