import { useEffect, useState } from 'react';
import { getUser, setUser } from "./backend.js";

export function UserSelector() {

    // Make the new select setting stick on the server
    function onUserChange(e) {
        destroyVariantSession()
        setCurrentUser(e.target.value);
    }

    // Close current variant session by deleting the session id cookie.
    function destroyVariantSession() {
        document.cookie = "variant-ssnid=;host=loclhost;path=/;expires=" + new Date(0).toUTCString()
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
              onChange={e => onUserChange(e)}
            >
              <option value="NoReputation">NoReputation</option>
              <option value="WithReputation">WithReputation</option>
            </select>
            <a href="#" onClick={e => destroyVariantSession()}>Re-login</a>
            </label>
        );
    } else {
        return null;
    }
}
