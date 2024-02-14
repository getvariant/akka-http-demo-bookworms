import { useEffect, useState } from 'react';
import { getUser, setUser, getPromoMessage } from "./backend.js";

export function UserSelector() {

    function onChangeListener(e) {
        // Close current variant session by deleting the session id cookie.
        document.cookie = "variant-ssnid=;host=loclhost;path=/;expires=" + new Date(0).toUTCString()
        // Make the new select setting stick
        setCurrentUser(e.target.value);
    }

    const [currentUser, setCurrentUser] = useState(null);
    useEffect(() => {
        const fetchData = async () => {
          const currUser = await getUser()
          setCurrentUser(currUser)
        };
        fetchData()
      }, []);

    if (currentUser) {
        return (
            <label id={'user-select'}>
            Current user:
            <select
              value={currentUser}
              onChange={e => onChangeListener(e)}
            >
              <option value="NoReputation">NoReputation</option>
              <option value="WithReputation">WithReputation</option>
            </select>
            </label>
        );
    } else {
        return null;
    }
}

export function PromoMessage() {

    // This API call happens outside the context of a particular page and concurrently with the main
    // page API call. This presents a race condition: we want the main call to have completed and returned
    // the session ID cookie before this call is made, so that the session ID cookie would be available on
    // the backend of this call, so that Variant session, and with it the current state request be available.
    const [promoMessage, setPromoMessage] = useState(null);
    useEffect(() => {
      setTimeout(
        () => {
          const fetchData = async () => {
            const promoMessage = await getPromoMessage()
            setPromoMessage(promoMessage)
          };
          fetchData()
        }, 200)
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
