package xyz.easygiveaway.mysql;



public class SQLManager {

    private MySQL mySQL;
    public static String maintable = "main";
    public static String giveawaytable = "giveaways";
    public static String invitetable = "invites";

    public SQLManager(MySQL mySQL) {
        this.mySQL = mySQL;
    }


    public void createMainTable() {
        MySQL.update("CREATE TABLE IF NOT EXISTS " + maintable + " (guildId BIGINT NOT NULL, roleId BIGINT NOT NULL, color INT DEFAULT 0x5634cf, emoji TEXT DEFAULT '🎉');");
    }
    public void createGiveawayTable() {
        MySQL.update("CREATE TABLE IF NOT EXISTS " + giveawaytable + "(guildId BIGINT NOT NULL, messageId BIGINT NOT NULL, channelId BIGINT NOT NULL, emoji TEXT DEFAULT '🎉', winnerAmount INT DEFAULT 1);");
    }
    public void createInviteTable() {
        MySQL.update("CREATE TABLE IF NOT EXISTS " + invitetable + " (guildId BIGINT NOT NULL, memberId BIGINT NOT NULL, invites INT DEFAULT 0, fakes INT DEFAULT 0, adds INT DEFAULT 0);");
    }
}
