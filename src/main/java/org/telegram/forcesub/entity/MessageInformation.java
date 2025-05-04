package org.telegram.forcesub.entity;

/**
 * This Enum to mapping in database to send message
 * Bot not save the message in memory, it's save all information
 * Like Welcome Message, Help Message, Start Message, About Message
 * U can set in application.properties, In future, this enum data can updated
 * While Bot Running, Never Worry configuration will deleted,
 * Because all data saved in database, in application.properties, only save initial data
 */

public enum MessageInformation {

    WELCOME,
    HELP,
    START,
    ABOUT

}
