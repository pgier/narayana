/*
 * JBoss, Home of Professional Open Source
 * Copyright 2007, Red Hat Middleware LLC, and individual contributors
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
//
// Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003
//
// Arjuna Technologies Ltd.,
// Newcastle upon Tyne,
// Tyne and Wear,
// UK
//

package org.jboss.jbossts.qa.CrashRecovery05Servers;

/*
 * Copyright (C) 1999-2001 by HP Bluestone Software, Inc. All rights Reserved.
 *
 * HP Arjuna Labs,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: Server08.java,v 1.4 2003/07/15 15:33:12 jcoleman Exp $
 */

/*
 * Try to get around the differences between Ansi CPP and
 * K&R cpp with concatenation.
 */

/*
 * Copyright (C) 1999-2001 by HP Bluestone Software, Inc. All rights Reserved.
 *
 * HP Arjuna Labs,
 * Newcastle upon Tyne,
 * Tyne and Wear,
 * UK.
 *
 * $Id: Server08.java,v 1.4 2003/07/15 15:33:12 jcoleman Exp $
 */


import org.jboss.jbossts.qa.CrashRecovery05.*;
import org.jboss.jbossts.qa.CrashRecovery05Impls.AfterCrashServiceImpl02;
import org.jboss.jbossts.qa.Utils.OAInterface;
import org.jboss.jbossts.qa.Utils.ORBInterface;
import org.jboss.jbossts.qa.Utils.ServerIORStore;

public class Server08
{
	public static void main(String args[])
	{
		try
		{
			ORBInterface.initORB(args, null);
			OAInterface.initOA();

			AfterCrashServiceImpl02 afterCrashServiceImpl1 = new AfterCrashServiceImpl02(args[args.length - 3].hashCode(), 0);
			AfterCrashServiceImpl02 afterCrashServiceImpl2 = new AfterCrashServiceImpl02(args[args.length - 3].hashCode(), 1);

			AfterCrashServicePOATie servant1 = new AfterCrashServicePOATie(afterCrashServiceImpl1);
			AfterCrashServicePOATie servant2 = new AfterCrashServicePOATie(afterCrashServiceImpl2);

			OAInterface.objectIsReady(servant1);
			AfterCrashService afterCrashService1 = AfterCrashServiceHelper.narrow(OAInterface.corbaReference(servant1));

			OAInterface.objectIsReady(servant2);
			AfterCrashService afterCrashService2 = AfterCrashServiceHelper.narrow(OAInterface.corbaReference(servant2));

			ServerIORStore.storeIOR(args[args.length - 2], ORBInterface.orb().object_to_string(afterCrashService1));
			ServerIORStore.storeIOR(args[args.length - 1], ORBInterface.orb().object_to_string(afterCrashService2));

			System.out.println("Ready");

			ORBInterface.run();
		}
		catch (Exception exception)
		{
			System.err.println("Server08.main: " + exception);
			exception.printStackTrace(System.err);
		}
	}
}

