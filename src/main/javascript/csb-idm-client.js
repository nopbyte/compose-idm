var csb = require('./csb-client.js');


var topic_sub;

var csb_session = csb.createSession();

function message_listener(message, sender, msg_number) {
// the key must be one of these:
//
// * application
// * service_instance
// * service_source_code
// * service_object
// * service_composition
// * user


	console.log("Got Message: "+message.toString());
}

csb_session.on('event', function(type,value) {
	console.log("Got Event: "+type+" "+value);
	
	if (type === 'registered') {

		topic_sub = csb_session.createTopicSubscriber("IDMUPDATES", message_listener);
	}
});



