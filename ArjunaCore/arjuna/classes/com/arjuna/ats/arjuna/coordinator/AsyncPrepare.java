/*
 * JBoss, Home of Professional Open Source
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors 
 * as indicated by the @author tags. 
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors. 
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 * 
 * (C) 2005-2006,
 * @author JBoss Inc.
 */
/*
 * Copyright (C) 1998, 1999, 2000,
 *
 * Arjuna Solutions Limited,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.  
 *
 * $Id: AsyncPrepare.java 2342 2006-03-30 13:06:17Z  $
 */

package com.arjuna.ats.arjuna.coordinator;

import java.util.ArrayList;
import java.util.List;
import com.arjuna.ats.internal.arjuna.thread.ThreadActionData;

/**
 * Instances of this class are responsible for performing asynchronous prepare
 * on a specific AbstractRecord associated with a transaction.
 * 
 * @author Mark Little (mark@arjuna.com)
 * @version $Id: AsyncPrepare.java 2342 2006-03-30 13:06:17Z $
 * @since JTS 1.2.4.
 */

/*
 * Default visibility.
 */

class AsyncPrepare extends Thread {
	static List<AsyncPrepare> asyncPreparePool = new ArrayList<AsyncPrepare>();
	static {
		while (asyncPreparePool.size() < 100) {
			asyncPreparePool.add(new AsyncPrepare());
		}
	}

	public static AsyncPrepare create(BasicAction act,
			boolean reportHeuristics, AbstractRecord rec) {
		synchronized (asyncPreparePool) {
			while (asyncPreparePool.size() == 0) {
				try {
					asyncPreparePool.wait();
				} catch (InterruptedException e) {
					System.err
							.println("BORKED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					e.printStackTrace();
					System.exit(-1);
				}
			}
			AsyncPrepare asyncPrepare = asyncPreparePool.remove(0);
			asyncPrepare.initialize(act, reportHeuristics, rec);
			return asyncPrepare;
		}
	}

	public void run() {
		while (true) {
			synchronized (readyLock) {
				while (!ready) {
					try {
						readyLock.wait();
					} catch (InterruptedException e) {
						System.err
								.println("BORKED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
						e.printStackTrace();
						System.exit(-1);
					}
				}
				ready = false;
				/*
				 * This is a transient thread so we don't want to register it
				 * with the action it is preparing, only change its notion of
				 * the current transaction so that any abstract records that
				 * need that information can still have it.
				 */

				ThreadActionData.pushAction(_theAction, false);

				_outcome = _theAction.doPrepare(_reportHeuristics, _theRecord);

				ThreadActionData.popAction(false);

				_theRecord = null;
				_theAction = null;

				synchronized (resultLock) {
					result = true;
					resultLock.notify();
				}
			}
		}
	}

	public int outcome() {
		synchronized (resultLock) {
			while (!result) {
				try {
					resultLock.wait();
				} catch (InterruptedException e) {
					System.err
							.println("BORKED!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					e.printStackTrace();
					System.exit(-1);
				}
			}
			int outcome = _outcome;
			synchronized (asyncPreparePool) {
				asyncPreparePool.add(this);
				asyncPreparePool.notify();
			}
			result = false;
			return outcome;
		}
	}

	protected AsyncPrepare() {
		start();
	}

	private void initialize(BasicAction act, boolean reportHeuristics,
			AbstractRecord rec) {
		synchronized (readyLock) {
			_theAction = act;
			_outcome = TwoPhaseOutcome.PREPARE_NOTOK;
			_reportHeuristics = reportHeuristics;
			_theRecord = rec;
			ready = true;
			readyLock.notify();
		}
	}

	private boolean ready;
	private Object readyLock = new Object();
	private boolean result;
	private Object resultLock = new Object();
	private BasicAction _theAction;
	private int _outcome;
	private boolean _reportHeuristics;
	private AbstractRecord _theRecord;

};
