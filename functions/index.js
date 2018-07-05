const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendWelcomeMessage = functions.firestore.document('/users/{userID}')
.onWrite((change, context) => {
   const data = change.after.exists ? change.after.data() : null;
   const oldData = change.before.data();

    if (oldData!= null) {
       var oldMessagingToken = oldData.messagingToken;
       if (oldMessagingToken != null) {
        return;
       }
    }

    if (data != null) {
        var messagingToken = data.messagingToken;
        if (messagingToken == null)
            return;

        const payload = {
            notification: {
                title: 'Welcome to the value list',
                body: 'Thanks for using our app'
            }
        };

        return admin.messaging().sendToDevice(messagingToken, payload)
            .then(function(response) {
                console.log("Successfully sent message: ", response);
            })
            .catch(function(error) {
                console.log("Error sending message: ", error)
            });
    }

});