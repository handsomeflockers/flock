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
        console.log('sender = ' + sender);
        console.log('text = ' + text);

        //array to hold individual uids
        //const uids = [];
        const tokens = [];
        //We have the groupId
        //Now we need the uids of the members of that groups
        const groupMembersPromise = admin.database().ref(`/groups/${groupId}/members`).once('value');
        //returns a promise, so let's set code for when that's returned
        //we want to use the uids to access the rtdb and retrieve the users' tokens
        groupMembersPromise.then(snap => {
          console.log('snap = ' + snap);
          //we'll save them in uids
          const uids = Object.keys(snap.val());
          console.log('uids = ' + uids);
          console.log('uids[0] = ' + uids[0])
          //and then we return uids
          return uids;
        }).then(uids =>{
          uids.forEach(uid => {
            //for each uid, get its token
            console.log('uid in forEach = ' + uid);
            const getDeviceTokensPromise = admin.database().ref(`/flock-login/users/${uid}/token`).once('value')
            .then(snaps => {
              console.log('in');
              const tok = snaps.val().token;
              console.log('tok =' + tok);
              const payload = {
                  notification: {
                      title: sender,
                      body: text,
                      icon: sender
                  }
              };
              admin.messaging().sendToDevice(tok, payload)
                  .then(function (response) {
                      console.log("Successfully sent message:", JSON.stringify(response));
                  })
                  .catch(function (error) {
                      console.log("Error sending message:", error);
                  });

            });
          });
        });//end groupMembersPromise.then()


});//end exports.sendNotification()







/*


        //I want to get the uids of users in the groupId
        const getUidsOfGroupUsers = admin.database().ref(`/groups/${groupId}/members`).once('value');
        return Promise.all(getUidsOfGroupUsers).then(users =>{


            const usersUids = Object.keys(users.val());
            console.log('There are ' + users.numChildren() + ' users');

            for (var key in userUids) {

              const tok = admin.database().ref(`/users/{key}/token/token`).once('value');
              const token = Object.keys(tok.val());
              console.log('token = ' + token);
              return Promise.all(token).then(result => {

                const payload = {
                    notification: {
                        title: sender,
                        body: text,
                        icon: sender
                    }
                };
                admin.messaging().sendToDevice(token, payload)
                    .then(function (response) {
                        console.log("Successfully sent message:", response);
                    })
                    .catch(function (error) {
                        console.log("Error sending message:", error);
                    });

              });

            }




        });




        const getInstanceIdPromise = admin.database().ref(`/users/0FKtBCblmXPrUpKa1ruOXwmpaDC3/token`).once('value');
        const getSenderUidPromise = admin.auth().getUser('0FKtBCblmXPrUpKa1ruOXwmpaDC3');

        /*return Promise.all([getInstanceIdPromise, getSenderUidPromise]).then(results => {
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
*/