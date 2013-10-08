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

    public VisualizeVisitor(Database database) {
        this.database = database;
    }
    
    public VisualizeVisitor(Database database, ChangeExecListener execListener) {
      this(database);
      this.execListener = execListener;
    }

    @Override
    public Direction getDirection() {
        return ChangeSetVisitor.Direction.FORWARD;
    }

    @Override
    public void visit(ChangeSet changeSet, DatabaseChangeLog databaseChangeLog, Database database) throws LiquibaseException {
        ChangeSet.RunStatus runStatus = this.database.getRunStatus(changeSet);
        log.debug("Running Changeset:" + changeSet);
        fireWillRun(changeSet, databaseChangeLog, database, runStatus);
        
        List<Change> changes = changeSet.getChanges();
        for (Change change : changes) {
        	
        	String tableName = null;
        	try {
        		Method method = change.getClass().getMethod("getTableName");
        		tableName = (String) method.invoke(change);
			} catch (Exception e) {
				// nothing
			}

        	String referencedTableName = null;
        	try {
        		Method method = change.getClass().getMethod("getReferencedTableName");
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
        	
//        	AddPrimaryKeyChange changePK = (AddPrimaryKeyChange) change;
//        	changePK.getTableName()
//        	System.out.println(change.getClass().getName() + "             :  " + change.getChangeSet().getId() + "");
        	if (tableName != null)
        	{
        			System.out.println(tableName);
        	}
        	if (referencedTableName != null && baseTableName != null)
        	{
        		System.out.println(baseTableName + " -> " + referencedTableName);
        	}
		}
        
//        ChangeSet.ExecType execType = changeSet.execute(databaseChangeLog, this.database);
//        if (!runStatus.equals(ChangeSet.RunStatus.NOT_RAN)) {
//            execType = ChangeSet.ExecType.RERAN;
//        }
//        fireRan(changeSet, databaseChangeLog, database, execType);
        // reset object quoting strategy after running changeset
        this.database.setObjectQuotingStrategy(ObjectQuotingStrategy.LEGACY);
//        this.database.markChangeSetExecStatus(changeSet, execType);

        this.database.commit();
    }

    private void fireWillRun(ChangeSet changeSet, DatabaseChangeLog databaseChangeLog, Database database2, RunStatus runStatus) {
      if (execListener != null) {
        execListener.willRun(changeSet, databaseChangeLog, database, runStatus);
      }      
    }

    private void fireRan(ChangeSet changeSet, DatabaseChangeLog databaseChangeLog, Database database2, ExecType execType) {
      if (execListener != null) {
        execListener.ran(changeSet, databaseChangeLog, database, execType);
      }
    }
}
