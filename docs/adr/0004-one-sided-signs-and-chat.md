# 0004. One-sided signs, read in chat

Date: 2026-09-29
Status: Accepted

## Context

A billboard has no back, and with the text renderer gone the sign can't be
read in the world. Right-click in vanilla 1.20 opens the editor.

## Decision

- The front is the only side: back-text reads and writes are redirected to
  the front, and the player always faces the front.
- Right-click prints the front text in chat as `<Sign> line | line`, in the
  sign's dye color. Shift + right-click opens the editor; an empty sign
  opens it on plain right-click. Waxed signs can't be edited but can still
  be read.
- Default text color is white instead of black, so undyed text stays
  readable in chat. Glowing signs render fullbright.
- The server decides: the client's editor call is only a prediction and is
  dropped.

## Consequences

- Existing back text is hidden while `front_only_edit` is on.
- Chat reading needs the mod on the server; the client only predicts.
