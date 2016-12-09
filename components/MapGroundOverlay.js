"use strict"

var React = require("react")
var {
    PropTypes,
} = React

var ReactNative = require("react-native")
var {
    View,
    NativeMethodsMixin,
    requireNativeComponent,
    StyleSheet,
    Platform,
    NativeModules,
    Animated,
} = ReactNative

var resolveAssetSource = require("react-native/Libraries/Image/resolveAssetSource")
var AMapGroundOverlay = requireNativeComponent("AMapGroundOverlay", MapGroundOverlay)

var MapGroundOverlay = React.createClass({
    mixins: [NativeMethodsMixin],

    viewConfig: {
        uiViewClassName: "AMapGroundOverlay",
        validAttributes: {
            coordinate: true,
        },
    },

    propTypes: {
        ...View.propTypes,

        /**
         * A custom image to be used as the marker"s icon. Only local image resources are allowed to be
         * used.
         */
        image: PropTypes.any,

        /**
         * The coordinate for the marker.
         */
        coordinate: PropTypes.shape({
            /**
             * Coordinates for the anchor point of the marker.
             */
            latitude: PropTypes.number.isRequired,
            longitude: PropTypes.number.isRequired,
        }).isRequired,

        /**
         * Sets the anchor point for the marker.
         *
         * The anchor specifies the point in the icon image that is anchored to the marker"s position
         * on the Earth"s surface.
         *
         * The anchor point is specified in the continuous space [0.0, 1.0] x [0.0, 1.0], where (0, 0)
         * is the top-left corner of the image, and (1, 1) is the bottom-right corner. The anchoring
         * point in a W x H image is the nearest discrete grid point in a (W + 1) x (H + 1) grid,
         * obtained by scaling the then rounding. For example, in a 4 x 2 image, the anchor point
         * (0.7, 0.6) resolves to the grid point at (3, 1).
         *
         * For ios, see the `centerOffset` prop.
         *
         * @platform android
         */
        anchor: PropTypes.shape({
            /**
             * Offset to the callout
             */
            x: PropTypes.number.isRequired,
            y: PropTypes.number.isRequired,
        }),

        /**
         * Callback that is called when the user presses on the marker
         */
        onPress: PropTypes.func,
    },

    _onPress: function(e) {
        this.props.onPress && this.props.onPress(e)
    },

    render: function() {
        var image = undefined
        if (this.props.image) {
            image = resolveAssetSource(this.props.image) || {}
            image = image.uri
        }

        return ( <AMapGroundOverlay
                    {...this.props}
                    image = {image}
                    style = {[styles.marker, this.props.style]}
                    onPress = {this._onPress}
                />
        )
    },
})

var styles = StyleSheet.create({
    marker: {
        position: "absolute",
        backgroundColor: "transparent",
    },
})

MapGroundOverlay.Animated = Animated.createAnimatedComponent(MapGroundOverlay)

module.exports = MapGroundOverlay
