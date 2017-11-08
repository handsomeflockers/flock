let functions = require('firebase-functions');
let admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/messages/{groupId}/{pushId}')
    .onWrite(event => {
        const message = event.data.current.val();
        const groupId = event.params.groupId;
        const sender = message.sender;
        const text = message.message;
        const promises = [];


        const getInstanceIdPromise = admin.database().ref(`/users/0FKtBCblmXPrUpKa1ruOXwmpaDC3/token`).once('value');
        const getSenderUidPromise = admin.auth().getUser('0FKtBCblmXPrUpKa1ruOXwmpaDC3');

        return Promise.all([getInstanceIdPromise, getSenderUidPromise]).then(results => {
            const instanceId = results[0].val();
            const sender = results[1];
            console.log('notifying ' + receiverUid + ' about ' + message.body + ' from ' + senderUid);

            const payload = {
                notification: {
                    title: sender.displayName,
                    body: message.body,
                    icon: sender.photoURL
                }
            };

            admin.messaging().sendToDevice(instanceId, payload)
                .then(function (response) {
                    console.log("Successfully sent message:", response);
                })
                .catch(function (error) {
                    console.log("Error sending message:", error);
                });
        });
    });
