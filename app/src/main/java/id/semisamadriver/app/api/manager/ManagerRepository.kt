package id.semisamadriver.app.api.manager

import android.content.Context
import id.semisamadriver.app.api.repository.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class ManagerRepository(
    private val context: Context
): KodeinAware {

    override val kodein by closestKodein { context }

    val repositoryAuth: RepositoryAuth by instance()
    val repositoryPerson: RepositoryPerson by instance()
    val repositoryLocation: RepositoryLocation by instance()
    val repositoryProduct: RepositoryProduct by instance()
    val repositoryRoute: RepositoryRoute by instance()

}