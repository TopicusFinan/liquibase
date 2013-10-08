package liquibase.changelog.visitor;

import java.lang.reflect.Method;
import java.util.List;

import liquibase.change.Change;
import liquibase.change.core.AddPrimaryKeyChange;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.ChangeSet.ExecType;
import liquibase.changelog.ChangeSet.RunStatus;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.ObjectQuotingStrategy;
import liquibase.exception.LiquibaseException;
import liquibase.logging.LogFactory;
import liquibase.logging.Logger;

public class VisualizeVisitor implements ChangeSetVisitor {

	private Database database;

	private Logger log = LogFactory.getLogger();

	private ChangeExecListener execListener;

	private StringBuilder graphVizContentBuilder;

	public VisualizeVisitor(Database database,
			StringBuilder graphVizContentBuilder) {
		this.database = database;
		this.graphVizContentBuilder = graphVizContentBuilder;
	}

	@Override
	public Direction getDirection() {
		return ChangeSetVisitor.Direction.FORWARD;
	}

	@Override
	public void visit(ChangeSet changeSet, DatabaseChangeLog databaseChangeLog,
			Database database) throws LiquibaseException {
		ChangeSet.RunStatus runStatus = this.database.getRunStatus(changeSet);
		log.debug("Running Changeset:" + changeSet);
		fireWillRun(changeSet, databaseChangeLog, database, runStatus);

		List<Change> changes = changeSet.getChanges();
		for (Change change : changes) {

			try {
				Method method = change.getClass().getMethod("getTableName");
			} catch (Exception e) {
				// nothing
			}

			String referencedTableName = null;
			try {
				Method method = change.getClass().getMethod(
						"getReferencedTableName");
				referencedTableName = (String) method.invoke(change);
			} catch (Exception e) {
				// nothing
			}

			String baseTableName = null;
			try {
				Method method = change.getClass().getMethod("getBaseTableName");
				baseTableName = (String) method.invoke(change);
			} catch (Exception e) {
				// nothing
			}

			if (referencedTableName != null && baseTableName != null) {
				graphVizContentBuilder.append("\t" + baseTableName + " -> "
						+ referencedTableName + "\n");
			}
		}

		this.database.setObjectQuotingStrategy(ObjectQuotingStrategy.LEGACY);

		this.database.commit();
	}

	private void fireWillRun(ChangeSet changeSet,
			DatabaseChangeLog databaseChangeLog, Database database2,
			RunStatus runStatus) {
		if (execListener != null) {
			execListener.willRun(changeSet, databaseChangeLog, database,
					runStatus);
		}
	}
}
