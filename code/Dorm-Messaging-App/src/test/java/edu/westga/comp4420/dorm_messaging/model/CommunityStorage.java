package edu.westga.comp4420.dorm_messaging.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Converts dorm selections, messages, and replies to and from storage values.
 *
 * @author Valai Willie
 */
public class CommunityStorage {

    private static final String DORM = "dorm";
    private static final String MESSAGE = "message";
    private static final String REPLY = "reply";
    private static final String ID = "id";
    private static final String AUTHOR = "author";
    private static final String CONTENT = "content";
    private static final String TIME = "time";
    private static final String PARENT = "parent";
    private static final String COUNT = "count";

    /**
     * Saves community data into the supplied properties.
     *
     * @param dorms the dorm selections
     * @param messages the message service
     * @param replies the reply service
     * @param data the destination properties
     */
    public void save(DormService dorms, MessageService messages,
            ReplyService replies, Properties data) {
        int index = 0;
        for (Map.Entry<Long, Dorm> entry : dorms.getSelections().entrySet()) {
            String prefix = this.prefix(DORM, index++);
            data.setProperty(prefix + ID, entry.getKey().toString());
            data.setProperty(prefix + DORM, entry.getValue().name());
        }
        data.setProperty(DORM + COUNT, Integer.toString(index));

        index = 0;
        for (Message message : messages.getAllMessages()) {
            this.saveMessage(message, this.prefix(MESSAGE, index++), data);
        }
        data.setProperty(MESSAGE + COUNT, Integer.toString(index));

        index = 0;
        for (Reply reply : replies.getAllReplies()) {
            this.saveReply(reply, this.prefix(REPLY, index++), data);
        }
        data.setProperty(REPLY + COUNT, Integer.toString(index));
    }

    /**
     * Restores community data into empty services after accounts are loaded.
     *
     * @param accounts the restored accounts
     * @param dorms the empty dorm service
     * @param messages the empty message service
     * @param replies the empty reply service
     * @param data the saved properties
     * @throws IllegalArgumentException if saved records are invalid
     */
    public void load(AccountRegistry accounts, DormService dorms,
            MessageService messages, ReplyService replies, Properties data) {
        Map<Long, Account> authors = new HashMap<>();
        for (Account account : accounts.getAccounts()) {
            authors.put(account.getId(), account);
        }
        this.loadDorms(authors, dorms, data);

        Map<Long, Message> parents = new HashMap<>();
        for (int index = 0; index < this.count(MESSAGE, data); index++) {
            Message message = this.readMessage(
                this.prefix(MESSAGE, index), authors, data);
            messages.restoreMessage(message);
            parents.put(message.getId(), message);
        }
        for (int index = 0; index < this.count(REPLY, data); index++) {
            Reply reply = this.readReply(
                this.prefix(REPLY, index), authors, parents, data);
            replies.restoreReply(reply);
        }
    }

    private void saveMessage(Message message, String prefix, Properties data) {
        data.setProperty(prefix + ID, Long.toString(message.getId()));
        data.setProperty(prefix + AUTHOR, Long.toString(message.getAuthor().getId()));
        data.setProperty(prefix + DORM, message.getDorm().name());
        data.setProperty(prefix + CONTENT, message.getContent());
        data.setProperty(prefix + TIME, message.getPostedAt().toString());
    }

    private void saveReply(Reply reply, String prefix, Properties data) {
        data.setProperty(prefix + ID, Long.toString(reply.getId()));
        data.setProperty(prefix + AUTHOR, Long.toString(reply.getAuthor().getId()));
        data.setProperty(prefix + PARENT, Long.toString(reply.getParentMessage().getId()));
        data.setProperty(prefix + CONTENT, reply.getContent());
        data.setProperty(prefix + TIME, reply.getPostedAt().toString());
    }

    private void loadDorms(Map<Long, Account> authors,
            DormService dorms, Properties data) {
        for (int index = 0; index < this.count(DORM, data); index++) {
            String prefix = this.prefix(DORM, index);
            long accountId = Long.parseLong(this.required(data, prefix + ID));
            if (!authors.containsKey(accountId)) {
                throw new IllegalArgumentException("Saved dorm account does not exist.");
            }
            dorms.restoreSelection(accountId,
                Dorm.valueOf(this.required(data, prefix + DORM)));
        }
    }

    private Message readMessage(String prefix,
            Map<Long, Account> authors, Properties data) {
        long id = Long.parseLong(this.required(data, prefix + ID));
        long authorId = Long.parseLong(this.required(data, prefix + AUTHOR));
        return new Message(id, authors.get(authorId),
            Dorm.valueOf(this.required(data, prefix + DORM)),
            this.required(data, prefix + CONTENT),
            LocalDateTime.parse(this.required(data, prefix + TIME)));
    }

    private Reply readReply(String prefix, Map<Long, Account> authors,
            Map<Long, Message> parents, Properties data) {
        long id = Long.parseLong(this.required(data, prefix + ID));
        long authorId = Long.parseLong(this.required(data, prefix + AUTHOR));
        long parentId = Long.parseLong(this.required(data, prefix + PARENT));
        return new Reply(id, parents.get(parentId), authors.get(authorId),
            this.required(data, prefix + CONTENT),
            LocalDateTime.parse(this.required(data, prefix + TIME)));
    }

    private String prefix(String type, int index) {
        return type + "." + index + ".";
    }

    private int count(String type, Properties data) {
        int result = Integer.parseInt(data.getProperty(type + COUNT, "0"));
        if (result < 0) {
            throw new IllegalArgumentException("Saved record counts cannot be negative.");
        }
        return result;
    }

    private String required(Properties data, String key) {
        String value = data.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing saved value: " + key);
        }
        return value;
    }
}