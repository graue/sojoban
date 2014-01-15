// parse levels in this format:
// Level N
// 'Optional Title Enclosed by Single Quotes'
// Level contents (non-empty lines)
//
// Level N+1
// etc. (with at least one blank line in between)

// command line args:
// input filename
// author name
// title of level set

// output goes to stdout

var fs = require('fs');

var levels = [];
var infile = process.argv[2];
var author = process.argv[3];
var title = process.argv[4];

var contents = fs.readFileSync(infile, {'encoding': 'utf-8'}).split('\n');
var lineNo = 0;
var ret;

while (lineNo < contents.length &&
       (ret = parseLevel(contents, lineNo, levels))) {
    lineNo += ret;
    while (lineNo < contents.length && isWhitespace(contents[lineNo]))
        lineNo++;
}

console.log(JSON.stringify({
    'author': author,
    'title': title,
    'levels': levels
}));

function isWhitespace(line) {
    return line.match(/^\s*$/);
}

function parseLevel(lines, startIndex, outArray) {
    var idx = startIndex;
    var title = null;
    var matches;
    var boardLines = [];
    var sokoChars = "# @$*.+";

    // Parse "Level N"
    if (!lines[idx].match(/level \d+/i)) {
        throw new Error("Line " + idx + ": Expected 'level N', "
                        + "found: " + lines[idx]);
    }
    idx++;

    // Possibly parse a level title
    if (matches = lines[idx].match(/^'([^']+)'$/)) {
        title = matches[1];
        idx++;
    }

    // Match Sokoban!
    while (!isWhitespace(lines[idx])) {
        boardLines.push(lines[idx]);
        if (!lines[idx].match(/^[# @$*\.+]+$/)) {
            console.warn('Line ' + idx + ': Warning, non-Sokoban '
                         + 'character found. Contents: '
                         + lines[idx]);
        }
        idx++;
    }

    var boardStr = boardLines.join('\n');
    outArray.push(title ? {'title': title, 'contents': boardStr}
                        : boardStr);

    return idx - startIndex;
}
