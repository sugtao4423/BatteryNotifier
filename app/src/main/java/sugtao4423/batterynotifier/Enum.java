package sugtao4423.batterynotifier;

public class Enum{

    public enum RuleType{
        NOTIFY,
        ACCOUNT
    }

    public enum AccountType{
        SLACK(0, "Slack"),
        TWITTER(1, "Twitter");

        private int position;
        private String text;

        private AccountType(final int position, final String text){
            this.position = position;
            this.text = text;
        }

        public int getPosition(){
            return position;
        }

        @Override
        public String toString(){
            return text;
        }
    }

    public enum SlackIconType{
        EMOJI("emoji"),
        URL("url");

        private String text;

        private SlackIconType(final String text){
            this.text = text;
        }

        @Override
        public String toString(){
            return text;
        }
    }

}
