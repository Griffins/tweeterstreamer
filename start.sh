echo "Attempting to start Streamer"
curl localhost:8082?command_type=3
terms=""
while true; do
    read -p "Enter track term , or done : " term
    case $term in
        done)
        break;;
		*) terms="$term+$terms";;
    esac
done

echo "Attempting to start server with entered terms"
uri="command_type=6&terms=$terms"
curl localhost:8082?$uri