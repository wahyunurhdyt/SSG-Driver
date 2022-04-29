package id.semisamadriver.app.api.manager

import android.content.Context
import id.semisamadriver.app.api.service.ServiceApi
import id.semisamadriver.app.api.service.ServiceAuth
import id.semisamadriver.app.api.service.ServicePerson
import id.semisamadriver.app.api.service.ServiceSrv
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class ManagerNetwork(
    private val context: Context
): KodeinAware {

    override val kodein by closestKodein { context }

    val serviceAuth: ServiceAuth by instance()
    val servicePerson: ServicePerson by instance()
    val servieceApi: ServiceApi by instance()
    val serviceSrv: ServiceSrv by instance()

}