package com.mill_e.test.ustwo;

import android.test.InstrumentationTestCase;

import com.mill_e.ustwo.DataModel.Message;
import com.mill_e.ustwo.DataModel.Messages;
import com.mill_e.ustwo.UsTwoService;

import junit.framework.Assert;

public class UsTwoServiceTest extends InstrumentationTestCase {

    private UsTwoService _service;
    private Messages _messages;

    public void setUp() throws Exception {
        super.setUp();
        _service = new UsTwoService();
        _messages = _service.getMessagesModel();
        _messages.setUpDatabase(getInstrumentation().getTargetContext());
    }

    public void tearDown() throws Exception {
        _messages.closeDatabase();
        _service = null;
        _messages = null;
    }

    public void testSetUpService() throws Exception {

    }

    public void testAddMessage() throws Exception {
        _service.addMessage("Test");
        Message message = _messages.getLastMessage();

        Assert.assertEquals(message.getMessageContent(), "Test");

        _service.addMessage("Next");
        message = _messages.getLastMessage();

        Assert.assertEquals(message.getMessageContent(), "Next");
    }

    public void testAddEvent() throws Exception {

    }

    public void testEditEvent() throws Exception {

    }

    public void testRemoveEvent() throws Exception {

    }

    public void testAddListItem() throws Exception {

    }

    public void testEditListItem() throws Exception {

    }

    public void testCheckListItem() throws Exception {

    }

    public void testRemoveListItem() throws Exception {

    }

    public void testCreateList() throws Exception {

    }

    public void testRemoveList() throws Exception {

    }

    public void testGetMessagesModel() throws Exception {

    }

    public void testGetEventsModel() throws Exception {

    }

    public void testGetListsModel() throws Exception {

    }

    public void testGetSettings() throws Exception {

    }

    public void testGetUsername() throws Exception {

    }
}
