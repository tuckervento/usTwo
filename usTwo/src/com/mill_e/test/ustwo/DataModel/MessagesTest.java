package com.mill_e.test.ustwo.DataModel;

import android.test.InstrumentationTestCase;

import com.mill_e.ustwo.DataModel.Message;
import com.mill_e.ustwo.DataModel.Messages;

public class MessagesTest extends InstrumentationTestCase {

    private Messages _messages;

    public void setUp() throws Exception {
        super.setUp();

        _messages = new Messages();
        _messages.setUpDatabase(getInstrumentation().getTargetContext());
    }

    public void tearDown() throws Exception {
        _messages.closeDatabase();
        _messages = null;
    }

    public void testIsEmpty() throws Exception {
        assertTrue(_messages.isEmpty());

        _messages.addMessage(new Message("Empty Test", 0));

        assertFalse(_messages.isEmpty());
    }

    public void testClearModel() throws Exception {
        _messages.addMessage(new Message("Clear Test", 0));

        assertFalse(_messages.isEmpty());

        _messages.clearModel();

        assertTrue(_messages.isEmpty());
    }

    public void testGetLastMessage() throws Exception {
        _messages.addMessage(new Message("GetLastTest", 0));

        assertEquals(_messages.getLastMessage().getMessageContent(), "GetLastTest");

        _messages.addMessage(new Message("GetLastTest 2", 0));

        assertEquals(_messages.getLastMessage().getMessageContent(), "GetLastTest 2");
    }

    public void testContainsMessage() throws Exception {
        Message testMessage = new Message("Contain Test", 0);
        _messages.addMessage(testMessage);

        assertTrue(_messages.containsMessage(testMessage));

        Message testMessage2 = new Message("Contain Test 2", 0);
        _messages.addMessage(testMessage);
        _messages.addMessage(testMessage);

        assertFalse(_messages.containsMessage(testMessage2));
    }
}
