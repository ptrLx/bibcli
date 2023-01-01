user> (bibcli.system/parse_bib_file "test2.bib")

[{:current_state 2,
  :current_line 9,
  :payload
  {"entrytype" "article",
   "pages" "1143--1148",
   "author" "P. J. Cohen",
   "citekey" "CitekeyArticle",
   "number" "6",
   "title" "The independence of the continuum hypothesis",
   "year" "1963",
   "volume" "50",
   "journal" "Proceedings of the National Academy of Sciences"}}
 {:current_state 2,
  :current_line 18,
  :payload
  {"entrytype" "book",
   "citekey" "ab94",
   "author" "Charalambos D. Aliprantis and Kim C. Border",
   "year" "1994",
   "title" "Infinite Dimensional Analysis",
   "publisher" "Springer",
   "address" "Berlin",
   "test" "test"}}
 {:current_state 2,
  :current_line 28,
  :payload
  {"entrytype" "booktest",
   "author" "Charalambos D. Aliprantis and Kim C. Border",
   "citekey" "newNameHere",
   "address" "Berlin",
   "title" "Infinite Dimensional Analysis",
   "year" "1994",
   "publisher" "Springer",
   "addition" "here",
   "test" "test"}}
 {:current_state 2,
  :current_line 39,
  :payload
  {"entrytype" "incollection",
   "pages" "1--10",
   "author" "Shapiro, Howard M.",
   "citekey" "CitekeyIncollection",
   "address" "New York, NY",
   "booktitle" "Flow Cytometry Protocols",
   "title" "Flow Cytometry: The Glass Is Half Full",
   "year" "2018",
   "publisher" "Springer",
   "editor" "Hawley, Teresa S. and Hawley, Robert G."}}]

user> (bibcli.system/bib_file (bibcli.system/parse_bib_file "test2.bib"))

["@article{CitekeyArticle,"
 "   pages      = \"1143--1148\""
 "   author     = \"P. J. Cohen\""
 "   number     = \"6\""
 "   title      = \"The independence of the continuum hypothesis\""
 "   year       = \"1963\""
 "   volume     = \"50\""
 "   journal    = \"Proceedings of the National Academy of Sciences\""
 "}"
 ""
 "@book{ab94,"
 "   author     = \"Charalambos D. Aliprantis and Kim C. Border\""
 "   year       = \"1994\""
 "   title      = \"Infinite Dimensional Analysis\""
 "   publisher  = \"Springer\""
 "   address    = \"Berlin\""
 "   test       = \"test\""
 "}"
 ""
 "@booktest{newNameHere,"
 "   author     = \"Charalambos D. Aliprantis and Kim C. Border\""
 "   address    = \"Berlin\""
 "   title      = \"Infinite Dimensional Analysis\""
 "   year       = \"1994\""
 "   publisher  = \"Springer\""
 "   addition   = \"here\""
 "   test       = \"test\""
 "}"
 ""
 "@incollection{CitekeyIncollection,"
 "   pages      = \"1--10\""
 "   author     = \"Shapiro, Howard M.\""
 "   address    = \"New York, NY\""
 "   booktitle  = \"Flow Cytometry Protocols\""
 "   title      = \"Flow Cytometry: The Glass Is Half Full\""
 "   year       = \"2018\""
 "   publisher  = \"Springer\""
 "   editor     = \"Hawley, Teresa S. and Hawley, Robert G.\""
 "}"
 ""]
