import static org.junit.Assert.*;

import org.junit.Test;

import com.arjuna.ats.arjuna.AtomicAction;
import com.arjuna.ats.arjuna.TopLevelAction;
import com.arjuna.ats.arjuna.coordinator.ActionStatus;


public class SimpleNestedTest {

	@Test
	public void testCommit() {
		AtomicAction A = new AtomicAction();
        AtomicAction B = new AtomicAction();
        AtomicAction C = new AtomicAction();
        
        A.begin();
        B.start(A); // circumvent begin, this will circumvent ThreadAction        
        C.start(A);
        
        A.commit();
        
        assertEquals(A.status() + "", A.status(), ActionStatus.ABORTED);
        assertEquals(B.status(), ActionStatus.ABORTED);
        
        assertEquals(C.status(), ActionStatus.ABORTED);
	}
	
	@Test
	public void testAbort() {
		AtomicAction A = new AtomicAction();
        AtomicAction B = new AtomicAction();
        AtomicAction C = new AtomicAction();
        
        A.begin();
        B.start(A); // circumvent begin, this will circumvent ThreadAction        
        C.start(A);
        
        A.abort();
        
        assertEquals(A.status(), ActionStatus.ABORTED);
        assertEquals(B.status(), ActionStatus.ABORTED);
        
        assertEquals(C.status(), ActionStatus.ABORTED);
	}

}
