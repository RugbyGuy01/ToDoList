package com.golfpvcc.todolist.lists.presentation

sealed interface ListOverviewAction {
    data object OnAddListClick : ListOverviewAction
    data object OnDismissNewListDialog : ListOverviewAction
    data class OnConfirmNewList(val name: String) : ListOverviewAction
    data class OnListClick(val listId: Long) : ListOverviewAction
    data class OnDeleteListClick(val list: TodoListUi) : ListOverviewAction
    data object OnConfirmDeleteList : ListOverviewAction
    data object OnDismissDeleteList : ListOverviewAction
    data object OnSearchClick : ListOverviewAction
}