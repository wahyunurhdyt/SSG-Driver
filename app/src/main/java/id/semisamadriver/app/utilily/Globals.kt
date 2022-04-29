package id.semisamadriver.app.utilily

import id.semisamadriver.app.R
import id.semisamadriver.app.api.data.DataAuth
import id.semisamadriver.app.api.data.Location
import id.semisamadriver.app.api.data.Route
import id.semisamadriver.app.base.Application

var tempAuth: DataAuth? = null
var tempRoutes: MutableList<Route>? = null
var tempLocation: Location? = null
var tempSelectedProduct: Boolean? = null
var tempAddress = Application.getString(R.string.labelFailedLoadingLocation)
var tempCategoryId: String? = null
var tempProductId: String? = null
var tempCategoryName: String? = null
var tempFragmenHasLoadedData: Boolean = false

var tempID: String = ""
var tempDeviceID: String = ""



