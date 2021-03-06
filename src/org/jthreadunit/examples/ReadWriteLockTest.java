// Copyright 2004 by Justin T. Sampson
//
// This file is part of JThreadUnit.
//
// JThreadUnit is free software; you can redistribute it and/or modify it under
// the terms of the GNU Lesser General Public License as published by the Free
// Software Foundation; either version 2.1 of the License, or (at your option)
// any later version.
//
// JThreadUnit is distributed in the hope that it will be useful, but WITHOUT
// ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
// FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
// for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with JThreadUnit; if not, write to the Free Software Foundation, Inc.,
// 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

package org.jthreadunit.examples;

import org.jthreadunit.TestThread;

import junit.framework.TestCase;


public class ReadWriteLockTest extends TestCase
{
    private ReadWriteLock lock;
    private ReadWriteLockTestThread thread1;
    private ReadWriteLockTestThread thread2;
    private ReadWriteLockTestThread thread3;
    private ReadWriteLockTestThread thread4;

    public void setUp()
    {
        lock = new ReadWriteLock();

        ThreadGroup group = new ThreadGroup(getName());
        thread1 = new ReadWriteLockTestThread(group, "thread1");
        thread2 = new ReadWriteLockTestThread(group, "thread2");
        thread3 = new ReadWriteLockTestThread(group, "thread3");
        thread4 = new ReadWriteLockTestThread(group, "thread4");

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }

    public void tearDown()
    {
        thread1.kill();
        thread2.kill();
        thread3.kill();
        thread4.kill();
    }

    public void testOneReader() throws Exception
    {
        thread1.performAction("acquireRead");
        thread1.performAction("releaseRead");
    }

    public void testOneWriter() throws Exception
    {
        thread1.performAction("acquireWrite");
        thread1.performAction("releaseWrite");
    }

    public void testTwoWriters() throws Exception
    {
        thread1.performAction("acquireWrite");
        thread2.actionShouldBlock("acquireWrite");
        thread1.performAction("releaseWrite");
        thread2.completeBlockedAction();
        thread2.performAction("releaseWrite");
    }

    public void testOneReaderOneWriter() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.actionShouldBlock("acquireWrite");
        thread1.performAction("releaseRead");
        thread2.completeBlockedAction();
        thread2.performAction("releaseWrite");
    }

    public void testOneWriterOneReader() throws Exception
    {
        thread1.performAction("acquireWrite");
        thread2.actionShouldBlock("acquireRead");
        thread1.performAction("releaseWrite");
        thread2.completeBlockedAction();
        thread2.performAction("releaseRead");
    }

    public void testTwoReaders() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.performAction("acquireRead");
        thread1.performAction("releaseRead");
        thread2.performAction("releaseRead");
    }

    public void testTwoReadersOneWriter() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.performAction("acquireRead");
        thread3.actionShouldBlock("acquireWrite");
        thread1.performAction("releaseRead");
        thread3.assertStillBlocked();
        thread2.performAction("releaseRead");
        thread3.completeBlockedAction();
        thread3.performAction("releaseWrite");
    }

    public void testOneWriterTwoReaders() throws Exception
    {
        thread1.performAction("acquireWrite");
        thread2.actionShouldBlock("acquireRead");
        thread3.actionShouldBlock("acquireRead");
        thread1.performAction("releaseWrite");
        thread2.completeBlockedAction();
        thread3.completeBlockedAction();
        thread2.performAction("releaseRead");
        thread3.performAction("releaseRead");
    }

    public void testFairToWriters() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.actionShouldBlock("acquireWrite");
        thread3.actionShouldBlock("acquireRead");
        thread1.performAction("releaseRead");
        thread3.assertStillBlocked();
        thread2.completeBlockedAction();
        thread2.performAction("releaseWrite");
        thread3.completeBlockedAction();
        thread3.performAction("releaseRead");
    }

    public void testWriterInterrupted() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.actionShouldBlock("acquireWrite");
        thread2.interrupt();
        thread1.performAction("releaseRead");
        thread3.performAction("acquireRead");
        thread3.performAction("releaseRead");
    }

    public void testWriterInterruptedNotifiesReaders() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.actionShouldBlock("acquireWrite");
        thread3.actionShouldBlock("acquireRead");
        thread4.actionShouldBlock("acquireRead");
        thread2.interrupt();
        thread3.completeBlockedAction();
        thread4.completeBlockedAction();
        thread1.performAction("releaseRead");
        thread3.performAction("releaseRead");
        thread4.performAction("releaseRead");
    }

    public void testReallyFunky() throws Exception
    {
        thread1.performAction("acquireRead");
        thread2.actionShouldBlock("acquireWrite");
        thread3.actionShouldBlock("acquireRead");
        thread4.actionShouldBlock("acquireWrite");
        thread2.interrupt();
        thread1.performAction("releaseRead");
        thread4.completeBlockedAction();
        thread4.performAction("releaseWrite");
        thread3.completeBlockedAction();
        thread3.performAction("releaseRead");
    }

    public class ReadWriteLockTestThread extends TestThread
    {
        public ReadWriteLockTestThread(ThreadGroup group, String name)
        {
            super(group, name);
        }

        public void doAcquireRead() throws InterruptedException
        {
            lock.acquireRead();
        }

        public void doReleaseRead()
        {
            lock.releaseRead();
        }

        public void doAcquireWrite() throws InterruptedException
        {
            lock.acquireWrite();
        }

        public void doReleaseWrite()
        {
            lock.releaseWrite();
        }
    }
}
