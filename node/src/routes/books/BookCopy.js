
export function BookCopy({copy}) {
    return (
        <tr key={copy.id}>
            <td>{copy.condition}</td>
            {copy.sellerReputation &&
              <td class="reputation">
                {'\u2605'.repeat(copy.sellerReputation) + '\u2606'.repeat(5 - copy.sellerReputation)}
              </td>
            }
            <td style={{"textAlign":"right"}}>{copy.price.toFixed(2)}</td>
            <td><a href={"/checkout/" + copy.id}>buy</a></td>
        </tr>
    )
}
