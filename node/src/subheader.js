import { useEffect, useState } from 'react';
import { getPromoMessage } from "./backend.js";

export function PromoMessage() {

    // This API call happens outside the context of a particular page and concurrently with the main
    // page API call. This presents a race condition: we want the main call to have completed and returned
    // the session ID cookie to the browser before this call is made, so that the session ID cookie would be
    // sent with this call and the backend could retrieve the Variant session, and with it the current state request.
    // To accomplish this, we repeat if the call returns a special token "repeat" which is server's way to
    // indicate that it didn't find the session. The proper way to handle this use case is to target for state
    // right from the Javascript code, and only then issue all other AJAX calls, when the Javascript client is here.
    const [promoMessage, setPromoMessage] = useState(null);
    useEffect(() => {
      const fetchData = async () => {
        var fromServer = "repeat"
        while (fromServer == "repeat") {
          fromServer = await getPromoMessage()
          console.log(fromServer)
        }
        setPromoMessage(fromServer)
       }
       fetchData()
      }, []);

    if (promoMessage) {
        return (
            <div id={'promo'}>
                {promoMessage}
            </div>
        );
    } else {
        return null;
    }
}
