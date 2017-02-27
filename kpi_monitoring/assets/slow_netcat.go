package main

import (
	"fmt"
	"log"
	"net"
	"net/http"
	"os"
	"time"
)

func main() {
	log.Print("creating listener")
	listener, err := net.Listen("tcp", ":12345")
	if err != nil {
		panic(err)
	}
	log.Print("done creating listener")

	go func() {
		for {
			log.Print("about to accept a connection")
			conn, err := listener.Accept()
			if err != nil {
				log.Fatal(err)
			}
			log.Print("done accepting a connection")
			go handleConn(conn)
		}
	}()

	http.HandleFunc("/", rootResponse)
	log.Print("listening...")
	err = http.ListenAndServe(":"+os.Getenv("PORT"), nil)
	if err != nil {
		panic(err)
	}
}

func handleConn(conn net.Conn) {
	for {
		log.Print("sleeping")
		time.Sleep(time.Second)
	}
}

func rootResponse(res http.ResponseWriter, req *http.Request) {
	fmt.Fprintf(res, "yo")
}
