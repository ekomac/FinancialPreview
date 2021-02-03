package com.ekosoftware.financialpreview.domain.local

import androidx.lifecycle.LiveData
import com.ekosoftware.financialpreview.data.local.daos.*
import com.ekosoftware.financialpreview.presentation.SelectionViewModel
import com.ekosoftware.financialpreview.presentation.SimpleQueryData
import javax.inject.Inject

class SelectionRepository @Inject constructor(
    private val accountDao: AccountDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val currencyDao: CurrencyDao,
    private val movementDao: MovementDao,
    private val settleGroupDao: SettleGroupDao
) {

    fun getSimpleQueryData(type: Int, queryText: String): LiveData<List<SimpleQueryData>> {
        return when (type) {
            SelectionViewModel.ACCOUNTS -> accountDao.getAccountsAsSimpleData(queryText)
            SelectionViewModel.BUDGETS -> budgetDao.getBudgetsAsSimpleData(queryText)
            SelectionViewModel.CATEGORIES -> categoryDao.getCategoriesAsSimpleData(queryText)
            SelectionViewModel.CURRENCIES -> currencyDao.getCurrenciesAsSimpleData(queryText)
            SelectionViewModel.MOVEMENTS -> movementDao.getMovementsAsSimpleData(queryText)
            SelectionViewModel.SETTLE_GROUPS -> settleGroupDao.getSettleGroupsAsSimpleData(queryText)
            else -> throw IllegalArgumentException("Error at: ${this.javaClass}. Given type $type isn't a value parameter.")
        }
    }
}