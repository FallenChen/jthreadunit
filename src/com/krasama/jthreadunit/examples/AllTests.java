package com.krasama.jthreadunit.examples;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{
    public static Test suite()
    {
        TestSuite testSuite = new TestSuite();
        testSuite.addTestSuite(SemaphoreTest.class);
        testSuite.addTestSuite(BrokenBoundedBufferTest.class);
        testSuite.addTestSuite(FixedBoundedBufferTest.class);
        testSuite.addTestSuite(ReadWriteLockTest.class);
        testSuite.addTestSuite(SynchronizedBlockTest.class);
        testSuite.addTestSuite(AssertionTest.class);
        return testSuite;
    }
}