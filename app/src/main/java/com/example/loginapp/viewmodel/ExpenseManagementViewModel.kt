package com.example.loginapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.loginapp.AddBudgetResponse
import com.example.loginapp.AddCategoryResponse
import com.example.loginapp.CategoryDefaultDataResponse
import com.example.loginapp.DeleteBudgetResponse
import com.example.loginapp.DeleteCategoryResponse
import com.example.loginapp.GetAllBudgetsResponse
import com.example.loginapp.GetAllCategoriesResponse
import com.example.loginapp.UpdateBudgetResponse
import com.example.loginapp.UpdateCategoryResponse
import com.example.loginapp.manager.ExpenseManager
import com.example.loginapp.model.ExpenseManagementRepository
import com.example.loginapp.model.ExpenseManagementRepository.UpdateCategoryCallback
import kotlinx.coroutines.launch

class ExpenseManagementViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository = ExpenseManagementRepository(ExpenseManager(application),application)

    // LiveData to hold the default categories list
    private val _categories = MutableLiveData<CategoryDefaultDataResponse>()
    val categories: LiveData<CategoryDefaultDataResponse> get() = _categories

    private val _allCategories = MutableLiveData<GetAllCategoriesResponse>()
    val allCategories: LiveData<GetAllCategoriesResponse> get() = _allCategories

    private val _addCategories = MutableLiveData<AddCategoryResponse>()
    val addCategories: LiveData<AddCategoryResponse> get() = _addCategories

    private val _deleteCategory = MutableLiveData<DeleteCategoryResponse>()
    val deleteCategory: LiveData<DeleteCategoryResponse> get() = _deleteCategory

    private val _updateCategory = MutableLiveData<UpdateCategoryResponse>()
    val updateCategory: LiveData<UpdateCategoryResponse> get() = _updateCategory

    private val _allBudgets = MutableLiveData<GetAllBudgetsResponse>()
    val allBudgets: LiveData<GetAllBudgetsResponse> get() = _allBudgets

    private val _addBudget = MutableLiveData<AddBudgetResponse>()
    val addBudget: LiveData<AddBudgetResponse> get() = _addBudget

    private val _deleteBudget = MutableLiveData<DeleteBudgetResponse>()
    val deleteBudget: LiveData<DeleteBudgetResponse> get() = _deleteBudget

    private val _updateBudget = MutableLiveData<UpdateBudgetResponse>()
    val updateBudget: LiveData<UpdateBudgetResponse> get() = _updateBudget

    // LiveData for error messages
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        // Optionally, load categories when the ViewModel is created
        fetchDefaultCategories()
    }

    // Load categories from the repository
    fun fetchDefaultCategories() {
        viewModelScope.launch {
            expenseRepository.getDefaultCategories(object : ExpenseManagementRepository.CategoryCallback {
                override fun onSuccess(response: CategoryDefaultDataResponse) {
                    _categories.postValue(response)
                }

                override fun onError(error: String) {
                    _error.postValue(error)
                }
            })
        }
    }
    // Load all categories from the ExpenseManagementRepository
    fun fetchAllCategories() {
        viewModelScope.launch {
            expenseRepository. getAllCategories(object : ExpenseManagementRepository.AllCategoriesCallback {
                override fun onSuccess(response: GetAllCategoriesResponse) {
                    _allCategories.postValue(response)
                }

                override fun onError(error: String) {
                    _error.postValue(error)
                }
            })
        }
    }

    //Add a category to the list
    fun addCategory(name: String, description: String) {
        expenseRepository.addCategory(name, description, object : ExpenseManagementRepository.AddCategoryCallback {
            override fun onSuccess(response: AddCategoryResponse) {
                _addCategories.postValue(response)
            }

            override fun onError(error: String) {
                _error.postValue(error)
            }
        })
    }

    //Delete Category from a list
    fun deleteCategory(id: String) {
        expenseRepository.deleteCategory(id, object : ExpenseManagementRepository.DeleteCategoryCallback {
            override fun onSuccess(response: DeleteCategoryResponse) {
                _deleteCategory.postValue(response)

                // refresh the categories list after deleting
                fetchAllCategories()
            }
            override fun onError(error: String) {
                _error.postValue(error)
            }
        })
    }

    // Update category in a list
    fun updateCategory(id: String, name: String, description: String, color_code: String) {
        expenseRepository.updateCategory(id, name, description, color_code, object : ExpenseManagementRepository.UpdateCategoryCallback {
            override fun onSuccess(response: UpdateCategoryResponse) {
                _updateCategory.postValue(response)
            }

            override fun onError(error: String) {
                _error.postValue(error)
            }
        })
    }

    /*
        Budgets
     */

    //Add a budget to the list
    fun addBudget(categoryId: String, amount: Float, start_date: String, end_date: String) {
        expenseRepository.addBudget(categoryId, amount, start_date, end_date, object : ExpenseManagementRepository.AddBudgetCallback {
            override fun onSuccess(response: AddBudgetResponse) {
                _addBudget.postValue(response)
            }

            override fun onError(error: String) {
                _error.postValue(error)
            }
        })
    }

    // Load all budgets from the ExpenseManagementRepository
    fun fetchAllBudgets() {
        viewModelScope.launch {
            expenseRepository.getAllBudgets(object : ExpenseManagementRepository.AllBudgetsCallback {
                override fun onSuccess(response: GetAllBudgetsResponse) {
                    _allBudgets.postValue(response)
                }

                override fun onError(error: String) {
                    _error.postValue(error)
                }
            })
        }
    }
    //Delete Budget from a category
    fun deleteBudget(id: String) {
        expenseRepository.deleteBudget(id, object : ExpenseManagementRepository.DeleteBudgetCallback {
            override fun onSuccess(response: DeleteBudgetResponse) {
                _deleteBudget.postValue(response)

                // Fetch the budget list data after deleting (refresh)
                fetchAllBudgets()
            }
            override fun onError(error: String) {
                _error.postValue(error)
            }
        })
    }
    // Update budget in category
    fun updateBudget(budgetId: String, categoryId: String, amount: Float, start_date: String, end_date: String) {
        expenseRepository.updateBudget( budgetId, categoryId, amount, start_date, end_date, object : ExpenseManagementRepository.UpdateBudgetCallback {
            override fun onSuccess(response: UpdateBudgetResponse) {
                _updateBudget.postValue(response)
            }

            override fun onError(error: String) {
                _error.postValue(error)
            }
        })
    }
}
