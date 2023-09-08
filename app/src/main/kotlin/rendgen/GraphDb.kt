package rendgen

import org.neo4j.driver.*

object GraphDb : AutoCloseable {
	// neo4j://(unencrypted) and neo4j+s:// work on either a single instance or a cluster.
	// Routing is handled by the driver. If used on a cluster, it routes to a cluster member,
	// not necessarily the system at the IP that you specified. Queries executed over that
	// protocol route according to the transaction functions --write transactions go to the
	// leader and read transactions route between followers and read replicas.
	private const val NEO4J_SERVER_ROOT_URI = "neo4j://localhost:7687"

	// bolt://(unencrypted) and bolt+s:// (encrypted with TLS) connect only to the server
	// with the IP you specify. It does not route anywhere else. All queries over this
	// protocol go only to this machine, whether they are read or write queries.
	// Write queries error out if not being sent to the Cluster leader.
	private const val BOLT_SERVER_ROOT_URI = "bolt://127.0.0.1:7687"

	private val driver: Driver

	init {
		driver = initDriver()
	}

	private fun authToken() = AuthTokens.basic("neo4j", "passw0rd")

	private fun initDriver(): Driver {
		println("Initializing Database...")

		return GraphDatabase.driver(NEO4J_SERVER_ROOT_URI, authToken())
			.also { driver ->
				driver.session().use { session ->
					session.executeWriteWithoutResult { tx ->
						tx.run("CREATE DATABASE rendgen IF NOT EXISTS")
					}
					println("Good DB.")
				}
			}
	}

	override fun close() {
		driver.close()
	}

	fun executeWriteWithoutResult(txConsumer: (TransactionContext) -> Unit) {
		driver.session(SessionConfig.forDatabase("rendgen")).use { session ->
			session.executeWriteWithoutResult(txConsumer)
		}
	}

}