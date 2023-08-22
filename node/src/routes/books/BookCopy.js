
export function BookCopy({copy, withReputation}) {
console.log(copy)
console.log(withReputation)
    return (
        <tr key={copy.id}>
            <td>{copy.condition}</td>
            {withReputation &&
              <td className="reputation">
                {'\u2605'.repeat(copy.reputation) + '\u2606'.repeat(5 - copy.reputation)}
              </td>
            }
            <td style={{"textAlign":"right"}}>{copy.price.toFixed(2)}</td>
            <td><a href={"/checkout/" + copy.id}>buy</a></td>
        </tr>
    )
}
