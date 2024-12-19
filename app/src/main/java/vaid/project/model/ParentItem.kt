package vaid.project.model

data class ParentItem(
    var title: String,
    val childItemList: List<User>,
    val isExpandable: Boolean = false
)