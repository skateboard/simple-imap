# Simple Imap
a simple imap client to listen for new incomming emails and filter them.

# Example Usage
```java
    public void testImap() throws MessagingException, IOException, InterruptedException {
        final Imap imap = new Imap(HOST, USERNAME, PASSWORD);

        final AtomicBoolean done = new AtomicBoolean(false);

        imap.addListener(new Listener(FROM, TO) {
            @Override
            public void onReceiveMessage(NewMessage message) {
                System.out.println(message.body());
                done.set(true);
            }
        });

        while (!done.get()) {
            System.out.println("Waiting for email...");
            TimeUnit.SECONDS.sleep(2);
        }
    }
```
